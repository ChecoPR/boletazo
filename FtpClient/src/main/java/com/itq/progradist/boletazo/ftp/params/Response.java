package com.itq.progradist.boletazo.ftp.params;

public class Response {
	public static final class Lugar {
		public static final class Informe {
			public static final String DAY = "day";
		}
		
		public static final class Values {
			public static final String ID_LUGAR = "idLugar";
			public static final String NOMBRE = "nombre";
			public static final String ESTADO = "estado";
			public static final String EVENTOS = "eventos";
		}
		
		public static final class Evento {
			public static final String ID_EVENTO = "idEvento";
			public static final String ID_LUGAR = "idLugar";
			public static final String NOMBRE = "nombre";
			public static final String FECHA = "fecha";
			public static final String HORA = "hora";
			public static final String TOTAL = "total";
		}
	}
}