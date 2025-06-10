package br.com.meubancodigitaljdbc.application.domain.model;

import br.com.meubancodigitaljdbc.application.domain.enuns.TipoCartao;

public class CartaoDebito extends Cartao{

    private double taxa = 0.05;
    private Conta conta;
    private double limiteDiario;
    private double totalPgtoHoje = 0;


    public CartaoDebito() {}

    public CartaoDebito(Conta conta, String numCartao, TipoCartao tipoCartao, int senha, double limiteDiario) {
        super();
        this.setConta(conta);
        this.setNumCartao(numCartao);
        this.setTipoCartao(tipoCartao);
        this.setSenha(senha);
        this.setLimiteDiario(limiteDiario);
    }

    public void alterarLimiteDebito(double novoLimite) {
        this.limiteDiario = novoLimite;

    }

    public void realizarPagamento(double pagamento) {
        if (!verificarStatus()) {
            System.out.println("Cartão Desativado.");
            return;
        }
        if (conta.getSaldo() >= pagamento && totalPgtoHoje + pagamento <= limiteDiario) {
            totalPgtoHoje += pagamento;
            double result = conta.getSaldo() - pagamento + (pagamento * taxa);
            System.out.println("Pagamento Efetuado com sucesso" + result);

        } else if (conta.getSaldo() < pagamento) {
            System.out.println("Saldo Insuficente...");
        } else {
            System.out.println("Limite diário excedido....");
        }
    }

    public void alterarLimiteDiario(double novoLimite) {
        this.limiteDiario = novoLimite;
    }


    public double getTaxa() {
        return taxa;
    }

    public void setTaxa(double taxa) {
        this.taxa = taxa;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public double getLimiteDiario() {
        return limiteDiario;
    }

    public void setLimiteDiario(double limiteDiario) {
        this.limiteDiario = limiteDiario;
    }

    public double getTotalPgtoHoje() {
        return totalPgtoHoje;
    }

    public void setTotalPgtoHoje(double totalPgtoHoje) {
        this.totalPgtoHoje = totalPgtoHoje;
    }


}
