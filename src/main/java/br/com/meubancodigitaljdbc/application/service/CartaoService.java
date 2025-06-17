package br.com.meubancodigitaljdbc.application.service;


import br.com.meubancodigitaljdbc.adapters.input.controllers.request.CompraCartaoRequest;
import br.com.meubancodigitaljdbc.application.domain.enuns.TipoCartao;
import br.com.meubancodigitaljdbc.application.domain.exceptions.*;
import br.com.meubancodigitaljdbc.application.domain.model.Cartao;
import br.com.meubancodigitaljdbc.application.domain.model.CartaoCredito;
import br.com.meubancodigitaljdbc.application.domain.model.CartaoDebito;
import br.com.meubancodigitaljdbc.application.domain.model.Conta;
import br.com.meubancodigitaljdbc.application.ports.input.usecases.CartaoUseCase;
import br.com.meubancodigitaljdbc.application.ports.output.repository.CartaoRepositoryPort;
import br.com.meubancodigitaljdbc.utils.CartaoUtils;
import br.com.meubancodigitaljdbc.utils.CategoriaLimiteUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class CartaoService implements CartaoUseCase {

    private final CartaoRepositoryPort cartaoRepositoryPort;

    private final ContaService contaService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CartaoService.class);

    @Autowired
    public CartaoService(CartaoRepositoryPort cartaoRepositoryPort, ContaService contaService) {
        this.contaService = contaService;
        this.cartaoRepositoryPort = cartaoRepositoryPort;


    }


    public void salvarCartao(Cartao cartao) throws CartaoNuloException, SQLException {
        if (cartao == null || cartao.getNumCartao() == null) {
            throw new CartaoNuloException("Erro: Tentativa de Salvar o Cartão nulo");
        }
            cartaoRepositoryPort.save(cartao);
    }

    public Cartao criarCartao(String contaC, TipoCartao tipoCartao, int senha, String diaVencimento)
            throws SQLException {

        Conta conta = contaService.buscarContas(contaC);
        LOGGER.info("Conta encontrada: {}, Tipo: {}", contaC, tipoCartao);
        if (conta == null) {
            throw new IllegalArgumentException("Erro: Cliente não pode ser null.");
        }

        LOGGER.info("Iniciando criação de cartão. Conta: {}, Tipo: {}", contaC, tipoCartao);

        String numCartao = gerarNumeroCartao();
        Cartao cartao;

        if (tipoCartao == TipoCartao.CREDITO) {
            cartao = criarCartaoCredito(conta, senha, numCartao, diaVencimento);
        } else if (tipoCartao == TipoCartao.DEBITO) {
            cartao = criarCartaoDebito(conta, senha, numCartao);
        } else {
            throw new IllegalArgumentException("Tipo de cartão inválido.");
        }

        LOGGER.info("Cartão criado com sucesso. Número: {}", cartao.getNumCartao());
        return cartaoRepositoryPort.save(cartao);
    }


    public Cartao criarCartaoCredito(Conta conta, int senha, String numCartao, String diaVencimento) {
        double limiteCredito = CategoriaLimiteUtils.limiteCredito(conta.getCliente().getCategoria());
        LocalDate dataVencimento = calcularDataVencimento(diaVencimento);

        return new CartaoCredito(
                conta,
                senha,
                numCartao,
                TipoCartao.CREDITO,
                limiteCredito,
                diaVencimento,
                dataVencimento
        );
    }

    public Cartao criarCartaoDebito(Conta conta, int senha, String numCartao) {
        double limiteDiario = CategoriaLimiteUtils.limiteDiario(conta.getCliente().getCategoria());

        return new CartaoDebito(
                conta,
                numCartao,
                TipoCartao.DEBITO,
                senha,
                limiteDiario
        );
    }


    public LocalDate calcularDataVencimento(String diaVencimento) {
        if (diaVencimento == null) {
            throw new IllegalArgumentException("Erro: O dia de vencimento é obrigatório para cartões de crédito.");
        }

        int dia = Integer.parseInt(diaVencimento);
        LocalDate hoje = LocalDate.now();

        try {
            LocalDate data = LocalDate.of(hoje.getYear(), hoje.getMonthValue(), dia);
            if (data.isBefore(hoje)) {
                return data.plusMonths(1);
            }
            return data;
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Erro: Dia de vencimento inválido.");
        }
    }



    public boolean alterarLimiteCartao(String numCartao, double novoLimite)
            throws CartaoStatusException, SQLException, CartaoNuloException, RegraNegocioException, CartaoFaturaException {

        Cartao cartao = validarCartao(numCartao, true);

        if (cartao instanceof CartaoCredito cartaoCredito) {
            cartaoCredito.alterarLimiteCredito(novoLimite);
        } else if (cartao instanceof CartaoDebito cartaoDebito) {
            cartaoDebito.alterarLimiteDebito(novoLimite);
        } else {
            throw new RegraNegocioException("Tipo de cartão não suportado para alteração de limite");
        }

        boolean sucesso = cartaoRepositoryPort.alterarLimiteCartao(numCartao, novoLimite);

        if (sucesso) {
            LOGGER.info("Limite alterado com sucesso para cartão {}: {}", numCartao, novoLimite);
        } else {
            LOGGER.warn("Falha ao alterar limite do cartão {}", numCartao);
        }

        return sucesso;
    }



    public boolean alterarStatus(String numCartao, boolean novoStatus)
            throws SQLException, CartaoStatusException, CartaoNuloException, CartaoFaturaException {

        validarCartao(numCartao, false);

        boolean sucesso = cartaoRepositoryPort.alterarStatusCartao(numCartao, novoStatus);

        if (sucesso) {
            LOGGER.info("Status alterado com sucesso para cartão {}: {}", numCartao, novoStatus);
        } else {
            LOGGER.warn("Falha ao alterar status do cartão {}", numCartao);
        }

        return sucesso;
    }



    public boolean alterarSenhaCartao(String numCartao, int senhaAntiga, int novaSenha)
            throws SQLException, CartaoStatusException, CartaoNuloException, CartaoFaturaException {

        validarCartao(numCartao, true);

        boolean sucesso = cartaoRepositoryPort.alterarSenhaCartao(numCartao, senhaAntiga, novaSenha);

        if (!sucesso) {
            throw new SQLException("Senha antiga incorreta");
        }

        return true;
    }


    public Cartao validarCartao(String numCartao, boolean checarStatusAtivo)
            throws CartaoNuloException, CartaoStatusException, SQLException, CartaoFaturaException {

        Optional<Cartao> cartaoOptional = Optional.ofNullable(cartaoRepositoryPort.buscarPorNumeroCartao(numCartao));
        if (cartaoOptional.isEmpty()) {
            throw new CartaoNuloException("Cartão não encontrado");
        }

        Cartao cartao = cartaoOptional.get();

        if (checarStatusAtivo && !cartao.isStatus()) {
            throw new CartaoStatusException("Cartão inativo.");
        }

        return cartao;
    }




    public String gerarNumeroCartao() {

        CartaoUtils numCartao = new CartaoUtils();

        return numCartao.gerarNumeroAleatorio(15) + numCartao.calcularDigitoLuhn(String.valueOf(15));
    }


    public boolean realizarCompra(CompraCartaoRequest dto)
            throws CartaoStatusException, SQLException, CartaoNuloException, CartaoFaturaException {

        LOGGER.info("Processando compra de {} no cartão {}", dto.getValor(), dto.getNumCartao());

        if (dto.getValor() <= 0) {
            return false;
        }

        Cartao cartao = validarCartao(dto.getNumCartao(), true);

        if (!(cartao instanceof CartaoCredito cartaoCredito)) {
            LOGGER.warn("Cartão {} não é de crédito. Compra não permitida.", dto.getNumCartao());
            return false;
        }

        boolean sucesso = cartaoCredito.realizarCompra(dto.getValor(), dto.getDataCompra());

        if (!sucesso) {
            LOGGER.warn("Compra negada: limite insuficiente para cartão {}", dto.getNumCartao());
            return false;
        }

        salvarCartao(cartaoCredito);
        LOGGER.info("Compra realizada com sucesso no cartão {}. Novo saldo: {}", dto.getNumCartao(), cartaoCredito.getSaldoMes());
        return true;
    }




    public void realizarPagamentoFatura(String numCartao, double valorPagamento) throws CartaoFaturaException, SQLException, CartaoStatusException, CartaoNuloException {
        Cartao cartao = cartaoRepositoryPort.buscarPorNumeroCartao(numCartao);
        LOGGER.info("Iniciando pagamento de fatura. Cartão: {}, Valor: {}", numCartao, valorPagamento);

        validarCartao(numCartao, true);

        if (!(cartao instanceof CartaoCredito cartaoCredito)) {
            throw new CartaoFaturaException("Cartão de crédito não encontrado ou tipo de cartão inválido.");
        }

        Conta conta = cartaoCredito.getConta();


        if (valorPagamento <= 0 || valorPagamento > cartaoCredito.getSaldoMes()) {
            throw new CartaoFaturaException("Valor do pagamento inválido.");
        }

        if (conta.getSaldo() < valorPagamento) {
            LOGGER.warn("Saldo insuficiente na conta para pagamento da fatura do cartão {}", numCartao);
            throw new CartaoFaturaException("Saldo insuficiente na conta para pagar a fatura.");
        }
        conta.setSaldo(conta.getSaldo() - valorPagamento);

        boolean pagamentoEfetuado = cartaoCredito.pagarFatura(valorPagamento);

        if (!pagamentoEfetuado) {
            throw new CartaoFaturaException("Não foi possível realizar o pagamento da fatura.");
        }
        LOGGER.info("Pagamento da fatura efetuado com sucesso no cartão {}", numCartao);

        salvarCartao(cartaoCredito);

    }

    public Cartao buscarCartaoPorCliente(String numCartao) throws CartaoFaturaException, CartaoStatusException, SQLException, CartaoNuloException {
        Optional<Cartao> cartaoOptional = Optional.ofNullable(cartaoRepositoryPort.buscarPorNumeroCartao(numCartao));
        return cartaoOptional.orElse(null);
    }

    public List<Cartao> buscarCartaoPorConta(String numeroConta) throws SQLException, ContaNaoEncontradaException, CartaoNaoEncontradoException {
        Conta conta = contaService.buscarContas(numeroConta);

        if (conta == null) {
            throw new ContaNaoEncontradaException("Conta não encontrada para o número: " + numeroConta);
        }

        List<Cartao> cartoes = cartaoRepositoryPort.buscarPorConta(conta);

        if (cartoes == null || cartoes.isEmpty()) {
            throw new CartaoNaoEncontradoException("Nenhum cartão encontrado para a conta: " + numeroConta);
        }
        LOGGER.info("Cartão encontrado: {} ", numeroConta);
        return cartoes;
    }


    public void consultarFatura(String numCartao) throws SQLException, CartaoFaturaException, CartaoStatusException, CartaoNuloException {
        Cartao cartao = cartaoRepositoryPort.buscarPorNumeroCartao(numCartao);

        if (!(cartao instanceof CartaoCredito cartaoCredito)) {
            throw new IllegalArgumentException("Cartão não é de crédito ou não encontrado");
        }
        LOGGER.info("Fatura atual do cartão {}: {}", numCartao, cartaoCredito.getSaldoMes());
    }


    public void deletarCartao(Long cartaoId) throws ClienteInvalidoException, SQLException {
        LOGGER.info("Tentando deletar cartao com ID: {}", cartaoId);
        Optional<Cartao> contaExistente = cartaoRepositoryPort.findById(cartaoId);

        if (contaExistente.isEmpty()) {
            LOGGER.warn("Cartao com ID {} não encontrado para deleção.", cartaoId);
            throw new ClienteInvalidoException("Cartao com ID " + cartaoId + " não encontrado.");
        }

        cartaoRepositoryPort.deleteById(cartaoId);
        LOGGER.info("Cartao com ID {} deletado com sucesso", cartaoId);
    }
}
