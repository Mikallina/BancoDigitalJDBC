package br.com.meubancodigitaljdbc.model;

    public class Endereco {

        private String logradouro;
        private Integer numero;
        private String complemento;
        public void setLocalidade(String localidade) {
            this.localidade = localidade;
        }

        private String cep;
        private String bairro;
        private String localidade;
        private String uf;

        public Endereco(String logradouro, Integer numero, String complemento, String cep, String bairro, String localidade,
                        String uf) {
            super();
            this.logradouro = logradouro;
            this.numero = numero;
            this.complemento = complemento;
            this.cep = cep;
            this.bairro = bairro;
            this.localidade = localidade;
            this.uf = uf;
        }

        public Endereco() {
        }

        public String getLogradouro() {
            return logradouro;
        }

        public void setLogradouro(String logradouro) {
            this.logradouro = logradouro;
        }

        public Integer getNumero() {
            return numero;
        }

        public void setNumero(Integer numero) {
            this.numero = numero;
        }

        public String getCep() {
            return cep;
        }

        public void setCep(String cep) {
            this.cep = cep;
        }

        public String getBairro() {
            return bairro;
        }

        public void setBairro(String bairro) {
            this.bairro = bairro;
        }

        public String getLocalidade() {
            return localidade;
        }

        public void setCidade(String localidade) {
            this.localidade = localidade;
        }

        public String getUf() {
            return uf;
        }

        public void setUf(String uf) {
            this.uf = uf;
        }

        public String complemento(String complemento) {
            return complemento;
        }

        public String getComplemento() {
            return complemento;
        }

        public void setComplemento(String complemento) {
            this.complemento = complemento;
        }
}
