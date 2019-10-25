package com.itq.progradist.boletazo;

public class ParamNames {
	
	public static final class Recurso {
		public static final String KEY_NAME = "recurso";
		
		public static final class Values {
		}
		
		public static final class Evento {

			public static final String VALUE = "eventos";
			
			public static final class Values {
				public static final String NOMBRE = "nombre";
				public static final String LUGAR = "lugar";
				public static final String ESTADO = "estado";
				public static final String FECHA = "fecha";
				public static final String HORA = "hora";
				public static final String PRECIO = "precio";
			}
		}
		
		public static final class EventoZona {

			public static final String VALUE = "evento/zonas";
			
			public static final class Values {
				public static final String ID_EVENTO = "id_evento";
			}
			
		}
		
		public static final class EventoZonaAsiento {

			public static final String VALUE = "evento/zonas/asientos";
			
			public static final class Values {
				public static final String ID_EVENTO = "id_evento";
				public static final String ID_ZONA = "id_zona";
			}
			
		}
		
		public static final class Apartado {

			public static final String VALUE = "apartado";
			
			public static final class Values {
				public static final String ID_ZONA = "zona_id";
				public static final String ID_EVENTO = "evento_id";
				public static final String ID_USUARIO = "usuario_id";
			}
			
			public static final class Boletos {
				public static final String ID_ASIENTO = "asiento_id";
				public static final String KEY = "num_boletos";
			}
			
		}
		
		public static final class Pago {
			
			public static final String VALUE = "pago";
			
			public static final class Metodo {
				public static final String POST = "post";
			}
			
			public static final class Values {
				public static final String METODO_PAGO = "metodo_pago";
				public static final String ID_APARTADO = "apartado_id";
			}
		}
	}
	
	public static final class Metodo {
		public static final String KEY_NAME = "metodo";
		public static final class Values {
			public static final String GET = "get";
			public static final String POST = "post";
		}
	}
}
