package edu.icesi.emprendimientos.carrito;

import java.io.Serializable;
import java.math.BigDecimal;

public class ItemCarrito implements Serializable {

    private Integer idProducto;
    private String nombreProducto;
    private String nombreEmprendimiento;
    private Integer idEmprendimiento;
    private BigDecimal precioUnitario;
    private Integer cantidad;

    public ItemCarrito() {}

    public ItemCarrito(Integer idProducto, String nombreProducto,
                       String nombreEmprendimiento, Integer idEmprendimiento,
                       BigDecimal precioUnitario, Integer cantidad) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.nombreEmprendimiento = nombreEmprendimiento;
        this.idEmprendimiento = idEmprendimiento;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
    }

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    // Getters y setters
    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public String getNombreEmprendimiento() { return nombreEmprendimiento; }
    public void setNombreEmprendimiento(String nombreEmprendimiento) { this.nombreEmprendimiento = nombreEmprendimiento; }

    public Integer getIdEmprendimiento() { return idEmprendimiento; }
    public void setIdEmprendimiento(Integer idEmprendimiento) { this.idEmprendimiento = idEmprendimiento; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
