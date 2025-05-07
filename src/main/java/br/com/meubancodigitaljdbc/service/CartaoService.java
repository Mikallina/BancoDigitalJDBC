package br.com.meubancodigitaljdbc.service;

import br.com.meubancodigitaljdbc.dao.CartaoDAO;
import br.com.meubancodigitaljdbc.dto.CompraCartaoDTO;
import br.com.meubancodigitaljdbc.enuns.Categoria;
import br.com.meubancodigitaljdbc.enuns.TipoCartao;
import br.com.meubancodigitaljdbc.execptions.*;
import br.com.meubancodigitaljdbc.model.Cartao;
import br.com.meubancodigitaljdbc.model.CartaoCredito;
import br.com.meubancodigitaljdbc.model.CartaoDebito;
import br.com.meubancodigitaljdbc.model.Conta;
import br.com.meubancodigitaljdbc.utils.GerarNumCartao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class CartaoService {

    private final CartaoDAO cartaoDAO;

    private final ContaService contaService;

    @Autowired
    public CartaoService(CartaoDAO cartaoDAO, ContaService contaService) {
        this.contaService = contaService;
        this.cartaoDAO = cartaoDAO;


    }


    public void salvarCartao(Cartao cartao, boolean isAtualizar) throws CartaoNuloException, SQLException {
        if (cartao == null || cartao.getNumCartao() == null) {
            throw new CartaoNuloException("Erro: Tentativa de Salvar o Cartão nulo");
        }
        if (isAtualizar) {
            cartaoDAO.atualizar(cartao);

        } else {
            cartaoDAO.save(cartao);
        }
    }

    public Cartao criarCartao(String contaC, TipoCartao tipoCartao, int senha, String diaVencimento) throws SQLException {
        Conta conta = contaService.buscarContas(contaC);

        if (conta == null) {
            throw new IllegalArgumentException("Erro: Cliente não pode ser null.");
        }


        LocalDate dataVencimento = null;
        if (tipoCartao == TipoCartao.CREDITO) {
            if (diaVencimento == null) {
                throw new IllegalArgumentException("Erro: O dia de vencimento é obrigatório para cartões de crédito.");
            }
            int dia = Integer.parseInt(diaVencimento);
            LocalDate dataAtual = LocalDate.now();
            int anoAtual = dataAtual.getYear();
            int mesAtual = dataAtual.getMonthValue();

            try {
                dataVencimento = LocalDate.of(anoAtual, mesAtual, dia);
            } catch (Exception e) {

            }

            if (dataVencimento.isBefore(dataAtual)) {
                if (mesAtual == 12) {
                    dataVencimento = LocalDate.of(anoAtual + 1, 1, dia);
                } else {
                    dataVencimento = LocalDate.of(anoAtual, mesAtual + 1, dia);
                }
            }
        }

        Cartao cartao;
        String numCartao = gerarNumeroCartao();

        if (tipoCartao == TipoCartao.CREDITO) {
            cartao = new CartaoCredito(conta, senha, numCartao, TipoCartao.CREDITO,
                    limiteDeCredito(conta), diaVencimento, dataVencimento);
        } else if (tipoCartao == TipoCartao.DEBITO) {
            cartao = new CartaoDebito(conta, numCartao, TipoCartao.DEBITO,
                    senha, limiteDiario(conta));
        } else {
            throw new IllegalArgumentException("Tipo de cartão inválido.");
        }

        return cartaoDAO.save(cartao);
    }

    public boolean alterarSenha(int senhaAntiga, int senhaNova, String numCartao)
            throws CartaoStatusException, CartaoNuloException, SQLException {

        Cartao cartao = buscarCartaoPorCliente(numCartao);

        if (cartao == null) {
            throw new CartaoNuloException("Cartão não pode ser nulo");
        }

        if (!cartao.isStatus()) {
            throw new CartaoStatusException("Status do Cartão Desativado");
        }

        if (cartao.getSenha() != senhaAntiga) {
            return false; // Senha antiga incorreta
        }

        cartao.setSenha(senhaNova);
        salvarCartao(cartao, true);
        return true;
    }


    public boolean alterarLimiteCartao(String numCartao, double novoLimite) throws CartaoStatusException, SQLException, CartaoNuloException {
        Optional<Cartao> cartaoOptional = Optional.ofNullable(cartaoDAO.buscarPorNumero(numCartao));

        if (cartaoOptional.isPresent()) {
            Cartao cartao = cartaoOptional.get();

            if (!cartao.isStatus()) {
                throw new CartaoStatusException("Status do Cartão Desativado");
            }

            if (cartao instanceof CartaoCredito cartaoCredito) {
                cartaoCredito.alterarLimiteCredito(novoLimite);
                salvarCartao(cartao, true);

                return true;
            } else if (cartao instanceof CartaoDebito cartaoDebito) {
                cartaoDebito.alterarLimiteDebito(novoLimite);
                salvarCartao(cartao, true);

                return true;
            }
        }

        return false;
    }

    public boolean alterarStatus(String numCartao, boolean novoStatus) throws CartaoNaoEncontradoException, SQLException, CartaoNuloException {
        Optional<Cartao> cartaoOptional = Optional.ofNullable(cartaoDAO.buscarPorNumero(numCartao));
        if (cartaoOptional.isPresent()) {
            Cartao cartao = cartaoOptional.get();
            cartao.setStatus(novoStatus);
            salvarCartao(cartao, true);
            return true;
        } else {
            throw new CartaoNaoEncontradoException("Cartão não encontrado com o ID fornecido.");
        }
    }

    private double limiteDeCredito(Conta conta) {
        Categoria categoria = conta.getCliente().getCategoria();
        switch (categoria) {
            case COMUM -> {
                return 1000.00;
            }
            case SUPER -> {
                return 5000.00;
            }
            case PREMIUM -> {
                return 10000.00;
            }
            default ->
                    throw new IllegalArgumentException("Categoria de Cliente desconhecida" + conta.getCliente().getCategoria());
        }
    }

    private double limiteDiario(Conta conta) {
        Categoria categoria = conta.getCliente().getCategoria();
        switch (categoria) {
            case COMUM -> {
                return 500.00;
            }
            case SUPER -> {
                return 1000.00;
            }
            case PREMIUM -> {
                return 5000.00;
            }
            default ->
                    throw new IllegalArgumentException("Categoria de Cliente desconhecida" + conta.getCliente().getCategoria());
        }
    }


	public String gerarNumeroCartao() {

        GerarNumCartao numCartao = new GerarNumCartao();

        return numCartao.gerarNumeroAleatorio(15) + numCartao.calcularDigitoLuhn(String.valueOf(15));
	}



    public boolean realizarCompra(CompraCartaoDTO dto)
            throws CartaoNaoEncontradoException, CartaoStatusException, SQLException, CartaoNuloException {

        if (dto.getValor() <= 0) {
            return false;
        }

        Cartao cartao = cartaoDAO.buscarPorNumero(dto.getNumCartao());

        if (cartao == null) {
            throw new CartaoNaoEncontradoException("Cartão não encontrado");
        }

        if (!cartao.isStatus()) {
            throw new CartaoStatusException("Status do cartão desativado");
        }

        if (!(cartao instanceof CartaoCredito)) {
            return false;
        }

        CartaoCredito cartaoCredito = (CartaoCredito) cartao;

        if (cartaoCredito.getLimiteCredito() <= dto.getValor()) {
            return false;
        }

        cartaoCredito.setSaldoMes(cartaoCredito.getSaldoMes() + dto.getValor());
        cartaoCredito.setSaldoCredito(cartaoCredito.getLimiteCredito() - dto.getValor());
        cartaoCredito.setPagamento(cartaoCredito.getPagamento() + dto.getValor());
        cartaoCredito.setDataCompra(dto.getDataCompra());

        salvarCartao(cartaoCredito, true);
        return true;
    }


    public boolean realizarPagamentoFatura(String numCartao, double valorPagamento) throws CartaoFaturaException, SQLException, CartaoStatusException, CartaoNuloException {
        Cartao cartao = cartaoDAO.buscarPorNumero(numCartao);

        if (!(cartao instanceof CartaoCredito)) {
            throw new CartaoFaturaException("Cartão de crédito não encontrado ou tipo de cartão inválido.");
        }

        if (!cartao.isStatus()) {
            throw new CartaoStatusException("Status do Cartão Desativado");
        }

        CartaoCredito cartaoCredito = (CartaoCredito) cartao;
        Conta conta = cartaoCredito.getConta();

        if (conta == null) {
            throw new CartaoFaturaException("Conta associada ao cartão não encontrada.");
        }

        if (valorPagamento <= 0 || valorPagamento > cartaoCredito.getSaldoMes()) {
            throw new CartaoFaturaException("Valor do pagamento inválido.");
        }

        if (conta.getSaldo() < valorPagamento) {
            throw new CartaoFaturaException("Saldo insuficiente na conta para pagar a fatura.");
        }

        // Debita da conta corrente
        conta.setSaldo(conta.getSaldo() - valorPagamento);

        // Realiza o pagamento da fatura no cartão
        boolean pagamentoEfetuado = cartaoCredito.pagarFatura(valorPagamento);

        if (!pagamentoEfetuado) {
            throw new CartaoFaturaException("Não foi possível realizar o pagamento da fatura.");
        }

        // Salva alterações no cartão e conta
        salvarCartao(cartaoCredito, true);
        //contaDAO.salvarConta(conta);

        return true;
    }

	public Cartao buscarCartaoPorCliente(String numCartao) throws SQLException {
		Optional<Cartao> cartaoOptional = Optional.ofNullable(cartaoDAO.buscarPorNumero(numCartao));
		return cartaoOptional.orElse(null);
	}

    public List<Cartao> buscarCartaoPorConta(String numeroConta) throws SQLException, ContaNaoEncontradaException, CartaoNaoEncontradoException {
        Conta conta = contaService.buscarContas(numeroConta);

        if (conta == null) {
            throw new ContaNaoEncontradaException("Conta não encontrada para o número: " + numeroConta);
        }

        List<Cartao> cartoes = cartaoDAO.buscarPorConta(conta);

        if (cartoes == null || cartoes.isEmpty()) {
            throw new CartaoNaoEncontradoException("Nenhum cartão encontrado para a conta: " + numeroConta);
        }

        return cartoes;
    }


    public double consultarFatura(String numCartao) throws SQLException {
        Cartao cartao = cartaoDAO.buscarPorNumero(numCartao);

        if (cartao == null) {
            throw new IllegalArgumentException("Cartão não encontrado");
        }

        if (cartao instanceof CartaoCredito) {

            CartaoCredito cartaoCredito = (CartaoCredito) cartao;

            return cartaoCredito.getSaldoMes();
        } else {

            throw new IllegalArgumentException("Cartão não é de crédito");
        }
    }

}
