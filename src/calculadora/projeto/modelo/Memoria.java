package calculadora.projeto.modelo;

import java.util.ArrayList;
import java.util.List;

public class Memoria {
	
	private enum TipoComando {
		ZERAR, NUMERO, DIV, MULT, SUB, SOMA, IGUAL, VIRGULA;
	};
	

     private static final Memoria instancia = new Memoria();
	
     private final List<MemoriaObservador> observadores = new ArrayList<>();
     
     private TipoComando ultimaOperecao = null;
     private boolean substituir = false;
     private String textoAtual = "";
     private String textoBuffer = "";
     
     private Memoria() {
    	 
     }
     
     public static Memoria getInstancia() {
    	 return instancia;
     }
     
     public void adicionarObservador(MemoriaObservador observador) {
    	 observadores.add(observador);
     }
     
     
     public String getTextoAtual() {
    	 return textoAtual.isEmpty() ? "0" : textoAtual;
     }
     
     public void provessarComando(String texto) {
    	 
    	 TipoComando tipoComando = detectarTipoComando(texto);
    	 
    	 if(tipoComando == null) {
    		 return;
    	 } else if (tipoComando == TipoComando.ZERAR) {
    		 textoAtual = "";
    		 textoBuffer = "";
    		 substituir = false;
    		 ultimaOperecao = null;
    	 } else if(tipoComando == TipoComando.NUMERO || tipoComando == TipoComando.VIRGULA) {
    		 textoAtual = substituir ? texto : textoAtual + texto;
    		 substituir = false;
    	 } else {
    		 substituir = true;
    		 textoAtual = obterResultadoOperacao();
    		 textoBuffer = textoAtual;
    		 ultimaOperecao = tipoComando;
    	 }
    	 
    	 observadores.forEach(o -> o.valorAlterado(getTextoAtual()));
     }

	private String obterResultadoOperacao() {
		if(ultimaOperecao == null || ultimaOperecao == TipoComando.IGUAL) {
			return textoAtual;
		}
		double numeroBuffer = Double.parseDouble(textoBuffer.replace(",", "."));
		double numeroAtual = Double.parseDouble(textoAtual.replace(",", "."));
		
		double resultado = 0;
		
		if(ultimaOperecao == TipoComando.SOMA) {
			resultado = numeroBuffer + numeroAtual;
		} else if(ultimaOperecao == TipoComando.SUB) {
			resultado = numeroBuffer - numeroAtual;
		} else if(ultimaOperecao == TipoComando.MULT) {
			resultado = numeroBuffer * numeroAtual;
		} else if(ultimaOperecao == TipoComando.DIV) {
				resultado = numeroBuffer / numeroAtual;
			}
		String texto = Double.toString(resultado).replace(".", ",");
		boolean inteiro = texto.endsWith(",0");
		return inteiro ? texto.replace(",0", "") : texto;
	}

	private TipoComando detectarTipoComando(String texto) {
		
		if(textoAtual.isEmpty() && texto == "0") {
			return null;

		}
		try {
			Integer.parseInt(texto);
			return TipoComando.NUMERO;
		} catch (NumberFormatException e) {
			// quando n for numero...
			if("AC".equals(texto)) {
				return TipoComando.ZERAR;
			} else if("/".equals(texto)) {
				return TipoComando.DIV;
			} else if("*".equals(texto)) {
				return TipoComando.MULT;
			} else if("+".equals(texto)) {
				return TipoComando.SOMA;
			} else if("-".equals(texto)) {
				return TipoComando.SUB;
			} else if("=".equals(texto)) {
				return TipoComando.IGUAL;
			} else if(",".equals(texto) && !textoAtual.contains(",")) {
				return TipoComando.VIRGULA;
		}
	}
		return null;
	}
    	}
    

