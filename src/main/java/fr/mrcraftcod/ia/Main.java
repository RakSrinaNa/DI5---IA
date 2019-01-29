package fr.mrcraftcod.ia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author hubert.cardot
 */
public class Main{
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	
	private final static int nbClasses = 3;
	private final static int nbCaract = 4;
	private final static int nbEx = 50;
	private final static int nbExApprent = 25;
	private final static int nbCouches = 3;
	private final static int nbCaches = 6;
	private final static int nbApprent = 2000;
	private final static int[] nbNeurones = {
			nbCaract + 1,
			nbCaches + 1,
			nbClasses
	}; //+1 pour neurone fixe
	private final static Double[][][] data = new Double[nbClasses][nbEx][nbCaract];
	private final static Double[][][] poids = new Double[nbCouches - 1][][];
	private final static Double[][] N = new Double[nbCouches][];
	private final static Double[][] S = new Double[nbCouches][];
	private final static Double coeffApprent = 0.01;
	private final static Double coeffSigmoide = 2.0 / 3;
	
	private static Double fSigmoide(Double x){
		return Math.tanh(coeffSigmoide * x);
	}
	
	private static Double dfSigmoide(Double x){
		return coeffSigmoide / Math.pow(Math.cosh(coeffSigmoide * x), 2);
	}
	
	public static void main(String[] args){
		LOGGER.info("Caches={} App={} coef={}", nbCaches, nbApprent, coeffApprent);
		initialisation();
		apprentissage();
		evaluation();
	}
	
	private static void initialisation(){
		readFile(Path.of("iris.data"));
		
		for(int couche = 0; couche < nbCouches - 1; couche++){
			poids[couche] = new Double[nbNeurones[couche + 1]][];
			for(int i = 0; i < nbNeurones[couche + 1]; i++){
				poids[couche][i] = new Double[nbNeurones[couche]];
				for(int j = 0; j < nbNeurones[couche]; j++){
					poids[couche][i][j] = (Math.random() - 0.5) / 10; //dans [-0,05; +0,05[
				}
			}
		}
		
		for(int couche = 0; couche < nbCouches; couche++){
			N[couche] = new Double[nbNeurones[couche]];
			S[couche] = new Double[nbNeurones[couche]];
		}
	}
	
	private static void apprentissage(){
		//---------- � faire
	}
	
	private static void evaluation(){
		int classeTrouvee, Ok = 0, PasOk = 0;
		for(int i = 0; i < nbClasses; i++){
			for(int j = nbExApprent; j < nbEx; j++){ // parcourt les ex. de test
				//---------- � faire              // calcul des N et S des neurones
				classeTrouvee = 0;                // recherche max parmi les sorties RN
				//---------- � faire
				//System.out.println("classe "+i+" classe trouv�e "+classeTrouvee);
				if(i == classeTrouvee){
					Ok++;
				}
				else{
					PasOk++;
				}
			}
		}
		System.out.println("Taux de reconnaissance : " + (Ok * 100. / (Ok + PasOk)));
	}
	
	private static void propagation(Double[] X){
		//---------- � faire
	}
	
	private static void retropropagation(int classe){
		//---------- � faire
	}
	
	private static void readFile(Path path){
		int classe = 0;
		int n = 0;
		try{
			for(String line : Files.readAllLines(path)){
				for(int i = 0; i < nbCaract; i++){
					String valCarac = line.substring(i * nbCaract, i * nbCaract + 3);
					data[classe][n][i] = Double.parseDouble(valCarac);
				}
				if(++n == nbEx){
					n = 0;
					classe++;
				}
			}
		}
		catch(Exception e){
			LOGGER.error("Error parsing file", e);
		}
	}
}
