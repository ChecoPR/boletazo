package com.itq.progradist.boletazo.database;

public class BoletazoDatabaseSchema {
	
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
		}
	}
}
