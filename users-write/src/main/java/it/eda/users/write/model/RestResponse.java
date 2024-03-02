package it.eda.users.write.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class RestResponse<T> implements Serializable, Dto {

	@Serial
	private static final long serialVersionUID = 6607754259039613847L;
	private transient T output;
	private Map<String, String> descrizioneErrore;
	private Map<String, Long> codiceErrore;

	public RestResponse(T output, Map<String, String> descrizioneErrore, Map<String, Long> codiceErrore) {
		super();
		this.output = output;
		this.descrizioneErrore = descrizioneErrore;
		this.codiceErrore = codiceErrore;
	}

	public T getOutput() {
		return output;
	}

	public void setOutput(T output) {
		this.output = output;
	}

	public Map<String, String> getDescrizioneErrore() {
		return descrizioneErrore;
	}

	public void setDescrizioneErrore(Map<String, String> descrizioneErrore) {
		this.descrizioneErrore = descrizioneErrore;
	}

	public Map<String, Long> getCodiceErrore() {
		return codiceErrore;
	}

	public void setCodiceErrore(Map<String, Long> codiceErrore) {
		this.codiceErrore = codiceErrore;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "RestResponse [descrizioneErrore=" + descrizioneErrore + ", codiceErrore=" + codiceErrore + "]";
	}

	public RestResponse() {
		super();
	}

}
