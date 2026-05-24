package com.beatify.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Registro de cada llamada a una API externa (MusicBrainz, Last.fm).
 * Sirve para auditoria, rate-limiting y depuracion.
 *
 * Valores validos:
 * <ul>
 *   <li>apiName: 'MUSICBRAINZ' o 'LASTFM' (CHECK en BD)</li>
 *   <li>metodoHttp: 'GET' o 'POST' o null</li>
 *   <li>exitoso: 'S' o 'N' (CHECK en BD)</li>
 * </ul>
 */
public class ApiCallLog {

    private Integer idApiCall;
    private String  apiName;            // 'MUSICBRAINZ' | 'LASTFM'
    private String  endpoint;
    private String  metodoHttp;         // 'GET' | 'POST' | null
    private String  parametros;
    private Integer codigoRespuesta;    // codigo HTTP
    private Integer duracionMs;
    private LocalDateTime fechaLlamada;
    private String  exitoso;            // 'S' | 'N'
    private String  mensajeError;
    private Integer idCliente;          // FK opcional a CLIENTE

    public ApiCallLog() {
    }

    public ApiCallLog(final String apiName, final String endpoint, final String metodoHttp,
                      final String parametros, final Integer codigoRespuesta, final Integer duracionMs,
                      final LocalDateTime fechaLlamada, final String exitoso, final String mensajeError,
                      final Integer idCliente) {
        this.apiName = apiName;
        this.endpoint = endpoint;
        this.metodoHttp = metodoHttp;
        this.parametros = parametros;
        this.codigoRespuesta = codigoRespuesta;
        this.duracionMs = duracionMs;
        this.fechaLlamada = fechaLlamada;
        this.exitoso = exitoso;
        this.mensajeError = mensajeError;
        this.idCliente = idCliente;
    }

    public ApiCallLog(final Integer idApiCall, final String apiName, final String endpoint,
                      final String metodoHttp, final String parametros, final Integer codigoRespuesta,
                      final Integer duracionMs, final LocalDateTime fechaLlamada, final String exitoso,
                      final String mensajeError, final Integer idCliente) {
        this.idApiCall = idApiCall;
        this.apiName = apiName;
        this.endpoint = endpoint;
        this.metodoHttp = metodoHttp;
        this.parametros = parametros;
        this.codigoRespuesta = codigoRespuesta;
        this.duracionMs = duracionMs;
        this.fechaLlamada = fechaLlamada;
        this.exitoso = exitoso;
        this.mensajeError = mensajeError;
        this.idCliente = idCliente;
    }

    public Integer getIdApiCall() { return this.idApiCall; }
    public void setIdApiCall(final Integer idApiCall) { this.idApiCall = idApiCall; }

    public String getApiName() { return this.apiName; }
    public void setApiName(final String apiName) { this.apiName = apiName; }

    public String getEndpoint() { return this.endpoint; }
    public void setEndpoint(final String endpoint) { this.endpoint = endpoint; }

    public String getMetodoHttp() { return this.metodoHttp; }
    public void setMetodoHttp(final String metodoHttp) { this.metodoHttp = metodoHttp; }

    public String getParametros() { return this.parametros; }
    public void setParametros(final String parametros) { this.parametros = parametros; }

    public Integer getCodigoRespuesta() { return this.codigoRespuesta; }
    public void setCodigoRespuesta(final Integer codigoRespuesta) { this.codigoRespuesta = codigoRespuesta; }

    public Integer getDuracionMs() { return this.duracionMs; }
    public void setDuracionMs(final Integer duracionMs) { this.duracionMs = duracionMs; }

    public LocalDateTime getFechaLlamada() { return this.fechaLlamada; }
    public void setFechaLlamada(final LocalDateTime fechaLlamada) { this.fechaLlamada = fechaLlamada; }

    public String getExitoso() { return this.exitoso; }
    public void setExitoso(final String exitoso) { this.exitoso = exitoso; }

    public String getMensajeError() { return this.mensajeError; }
    public void setMensajeError(final String mensajeError) { this.mensajeError = mensajeError; }

    public Integer getIdCliente() { return this.idCliente; }
    public void setIdCliente(final Integer idCliente) { this.idCliente = idCliente; }

    @Override
    public boolean equals(final Object o) {
        if (null == o || this.getClass() != o.getClass()) return false;
        final ApiCallLog that = (ApiCallLog) o;
        return Objects.equals(this.idApiCall, that.idApiCall);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idApiCall);
    }

    @Override
    public String toString() {
        return "ApiCallLog{" +
                "idApiCall=" + idApiCall +
                ", apiName='" + apiName + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", metodoHttp='" + metodoHttp + '\'' +
                ", codigoRespuesta=" + codigoRespuesta +
                ", duracionMs=" + duracionMs +
                ", fechaLlamada=" + fechaLlamada +
                ", exitoso='" + exitoso + '\'' +
                ", idCliente=" + idCliente +
                '}';
    }
}
