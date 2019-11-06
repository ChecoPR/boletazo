package com.itq.progradist.boletazo.ftp.params;

/**
 * Paramétros de la petición al servidor boletazo
 * 
 * @author Equipo 5
 *
 */
public class ParamNames {
	
	public static final class Recurso {
		public static final String KEY_NAME = "recurso";
		
		public static final class Values {
		}
		
		public static final class LugarVentas {

			public static final String VALUE = "lugar/ventas";
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
