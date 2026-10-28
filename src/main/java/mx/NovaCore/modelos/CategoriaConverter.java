package mx.NovaCore.modelos;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CategoriaConverter implements AttributeConverter<Categoria, String> {

    @Override
    public String convertToDatabaseColumn(Categoria categoria) {
        return categoria == null ? null : categoria.getValorBd();
    }

    @Override
    public Categoria convertToEntityAttribute(String valor) {
        return Categoria.desdeBd(valor);
    }

}
