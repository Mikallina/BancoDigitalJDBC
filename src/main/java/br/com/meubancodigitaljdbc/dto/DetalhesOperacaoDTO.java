package br.com.meubancodigitaljdbc.dto;

public class DetalhesOperacaoDTO {

	
	private String tipoOperacao;
    private String numConta;
    private String tipoConta;
    private double valorOperacao;
    private double taxaAplicada;
    private double saldoAntes;
    private double saldoDepois;
    private String mensagem;
	public String getTipoOperacao() {
		return tipoOperacao;
	}
	public void setTipoOperacao(String tipoOperacao) {
		this.tipoOperacao = tipoOperacao;
	}
	public String getNumConta() {
		return numConta;
	}
	public void setNumConta(String numConta) {
		this.numConta = numConta;
	}
	public String getTipoConta() {
		return tipoConta;
	}
	public void setTipoConta(String tipoConta) {
		this.tipoConta = tipoConta;
	}
	public double getValorOperacao() {
		return valorOperacao;
	}
	public void setValorOperacao(double valorOperacao) {
		this.valorOperacao = valorOperacao;
	}
	public double getTaxaAplicada() {
		return taxaAplicada;
	}
	public void setTaxaAplicada(double taxaAplicada) {
		this.taxaAplicada = taxaAplicada;
	}
	public double getSaldoAntes() {
		return saldoAntes;
	}
	public void setSaldoAntes(double saldoAntes) {
		this.saldoAntes = saldoAntes;
	}
	public double getSaldoDepois() {
		return saldoDepois;
	}
	public void setSaldoDepois(double saldoDepois) {
		this.saldoDepois = saldoDepois;
	}
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public DetalhesOperacaoDTO(String tipoOperacao, String numConta, String tipoConta, double valorOperacao,
			double taxaAplicada, double saldoAntes, double saldoDepois, String mensagem) {
		super();
		this.tipoOperacao = tipoOperacao;
		this.numConta = numConta;
		this.tipoConta = tipoConta;
		this.valorOperacao = valorOperacao;
		this.taxaAplicada = taxaAplicada;
		this.saldoAntes = saldoAntes;
		this.saldoDepois = saldoDepois;
		this.mensagem = mensagem;
	}

    

}
