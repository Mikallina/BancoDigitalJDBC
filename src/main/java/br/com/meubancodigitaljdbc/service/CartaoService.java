package br.com.meubancodigitaljdbc.service;

import br.com.meubancodigitaljdbc.dao.CartaoDAO;
import br.com.meubancodigitaljdbc.enuns.Categoria;
import br.com.meubancodigitaljdbc.enuns.TipoCartao;
import br.com.meubancodigitaljdbc.execptions.CartaoFaturaException;
import br.com.meubancodigitaljdbc.execptions.CartaoNaoEncontradoException;
import br.com.meubancodigitaljdbc.execptions.CartaoNuloException;
import br.com.meubancodigitaljdbc.execptions.CartaoStatusException;
import br.com.meubancodigitaljdbc.model.Cartao;
import br.com.meubancodigitaljdbc.model.CartaoCredito;
import br.com.meubancodigitaljdbc.model.CartaoDebito;
import br.com.meubancodigitaljdbc.model.Conta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;


@Service
public class CartaoService {

    private final CartaoDAO cartaoDAO;

	private static final Random RANDOM = new Random();


    @Autowired
    public CartaoService(CartaoDAO cartaoDAO) {

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

    public Cartao criarCartao(Conta conta, TipoCartao tipoCartao, int senha, String diaVencimento) throws SQLException {
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


    public boolean alterarSenha(int senhaAntiga, int senhaNova, Cartao cartao) throws CartaoStatusException, CartaoNuloException, SQLException {
        if (!cartao.isStatus()) {
            throw new CartaoStatusException("Status do Cartão Desativado");
        }
        if (cartao.getSenha() == senhaAntiga) {
            cartao.setSenha(senhaNova);
            salvarCartao(cartao, true);
            return true;
        } else {
            return true;
        }
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
		return gerarNumeroAleatorio(15) + calcularDigitoLuhn(gerarNumeroAleatorio(15));
	}

	private String gerarNumeroAleatorio(int tamanho) {
		StringBuilder numero = new StringBuilder();

		for (int i = 0; i < tamanho; i++) {
			numero.append(RANDOM.nextInt(10));
		}

		return numero.toString();
	}

	private int calcularDigitoLuhn(String numeroParcial) {
		int soma = 0;
		boolean alternar = false;
		for (int i = numeroParcial.length() - 1; i >= 0; i--) {
			int digito = Integer.parseInt(String.valueOf(numeroParcial.charAt(i)));

			if (alternar) {
				digito *= 2;
				if (digito > 9) {
					digito -= 9;
				}
			}

			soma += digito;
			alternar = !alternar;
		}

		return (10 - (soma % 10)) % 10;
	}

    public boolean realizarCompra(String numCartao, double valor, LocalDate dataCompra) throws CartaoNaoEncontradoException, CartaoStatusException, SQLException, CartaoNuloException {
        if (valor <= 0) {

            return false;
        }
        Cartao cartao = cartaoDAO.buscarPorNumero(numCartao);

        if (cartao == null) {
            throw new CartaoNaoEncontradoException("Cartão Não encontrado");
        }
        if (!cartao.isStatus()) {
            throw new CartaoStatusException("Status do Cartão Desativado");
        }

        if (cartao instanceof CartaoCredito) {
            CartaoCredito cartaoCredito = (CartaoCredito) cartao;

            if (cartaoCredito.getLimiteCredito() > valor) {
                cartaoCredito.setSaldoMes(cartaoCredito.getSaldoMes() + valor);
                cartaoCredito.setSaldoCredito(cartaoCredito.getLimiteCredito() - valor);
                cartaoCredito.setPagamento(cartaoCredito.getPagamento() + valor);

                cartaoCredito.setDataCompra(dataCompra);
                salvarCartao(cartao, true);

                System.out.println("Pagamento realizado com sucesso.");
                return true;
            } else {
                System.out.println("Limite de crédito excedido.");
                return false;
            }
        } else {
            System.out.println("Este cartão não é de crédito.");
            return false;
        }
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

    public List<Cartao> buscarCartaoPorConta(Conta conta) throws SQLException {
        return cartaoDAO.buscarPorConta(conta);
    }

    public Cartao buscarCartaoPorNumero(String numCartao) throws SQLException {

        return cartaoDAO.buscarPorNumero(numCartao);
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
