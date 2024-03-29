package com.itq.progradist.boletazo.database;

public class DatabaseSchema {
	
	public static final class LugarTable {
		public static final String NAME = "Lugar";
		
		public static final class Cols {
			public static final String ID_LUGAR = "idLugar";
			public static final String NOMBRE = "nombre";
			public static final String ESTADO = "estado";
		}
	}
	
	public static final class EventoTable {
		public static final String NAME = "Eventos";
		
		public static final class Cols {
			public static final String ID_EVENTO = "idEvento";
			public static final String ID_LUGAR = "idLugar";
			public static final String NOMBRE = "nombre";
			public static final String FECHA = "fecha";
			public static final String HORA = "hora";
			public static final String TOTAL = "total";
		}
	}
	
	public static final class EventoZonaTable {
		public static final String NAME = "EventosZonas";
		
		public static final class Cols {
			public static final String ID_LUGAR = "idLugar";
			public static final String ID_ZONA = "idZona";
			public static final String PRECIO = "precio";
			public static final String ID_EVENTO = "idEvento";
		}
	}
	
	public static final class EventoAsientoTable {
		public static final String NAME = "EventosAsientos";
		
		public static final class Cols {
			public static final String ID_APARTADO = "idApartado";
			public static final String ID_ZONA = "idZona";
			public static final String ID_ASIENTO = "idAsiento";
			public static final String ID_EVENTO = "idEvento";
		}
	}
	
	public static final class ApartadoTable {
		public static final String NAME = "Apartados";
		
		public static final class Cols {
			public static final String ID_APARTADO = "idApartado";
			public static final String ID_USUARIO = "idUsuario";
			public static final String ID_EVENTO = "idEvento";
			public static final String PAGADO = "pagado";
			public static final String TIEMPO = "tiempo";
			public static final String IMPORTE = "importe";
		}
	}
	
	public static final class MetodoPagoTable {
		public static final String NAME = "metodospago";
		
		public static final class Cols {
			public static final String ID_METODO_PAGO = "idMetodoPago";
			public static final String ID_USUARIO = "idUsuario";
			public static final String NUMERO_TARJETA = "numeroTarjeta";
			public static final String DOMICILIO = "domicilio";
			public static final String SALDO = "saldo";
		}
	}
	
	public static final class UsuarioTable {
		public static final String NAME = "usuarios";
		
		public static final class Cols {
			public static final String ID_USUARIO = "idUsuario";
			public static final String NOMBRE = "nombre";
			public static final String DIRECCION = "direccion";
			public static final String TELEFONO = "telefono";
			public static final String EMAIL = "email";
		}
	}
}
