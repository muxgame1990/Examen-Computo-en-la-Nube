package mx.NovaCore.modelos;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TipoMovimientoConverter implements AttributeConverter<TipoMovimiento, String> {

    @Override
    public String convertToDatabaseColumn(TipoMovimiento tipo) {
        return tipo == null ? null : tipo.getValor();
    }

    @Override
    public TipoMovimiento convertToEntityAttribute(String valor) {
        return TipoMovimiento.movimientos(valor);
    }
}
