package br.com.meubancodigitaljdbc.dto;

public class StatusDTO {
	
	  private boolean status;

	    // Construtores
	    public StatusDTO() {
	    }

	    public StatusDTO(boolean status) {
	        this.status = status;
	    }

	    // Getter e Setter
	    public boolean isStatus() {
	        return status;
	    }

	    public void setStatus(boolean status) {
	        this.status = status;
	    }

		public boolean isAtivo() {
			// TODO Auto-generated method stub
			return status;
		}

}
