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
	private final static double[][][] data = new double[nbClasses][nbEx][nbCaract];
	private final static double[][][] poids = new double[nbCouches - 1][][];
	private final static double[][] N = new double[nbCouches][];
	private final static double[][] S = new double[nbCouches][];
	private final static double coeffApprent = 0.01;
	private final static double coeffSigmoide = 2.0 / 3;
	
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
			poids[couche] = new double[nbNeurones[couche + 1]][];
			for(int i = 0; i < nbNeurones[couche + 1]; i++){
				poids[couche][i] = new double[nbNeurones[couche]];
				for(int j = 0; j < nbNeurones[couche]; j++){
					poids[couche][i][j] = (Math.random() - 0.5) / 10; //dans [-0,05; +0,05[
				}
			}
		}
		
		for(int couche = 0; couche < nbCouches; couche++){
			N[couche] = new double[nbNeurones[couche]];
			S[couche] = new double[nbNeurones[couche]];
		}
	}
	
	private static void apprentissage(){
		for(int klass = 0; klass < nbClasses; klass++){
			for(int apprentIndex = 0; apprentIndex < nbExApprent; apprentIndex++){
				double[] results = applyNetwork(data[klass][apprentIndex]);
				double[] errors = new double[results.length];
				for(int errorIndex = 0; errorIndex < errors.length; errorIndex++){
					errors[errorIndex] = Math.pow(results[errorIndex] - (klass == errorIndex ? 1 : 0), 2);
				}
				for(int errorIndex = 0; errorIndex < errors.length; errorIndex++){
					retropropagation(errorIndex, errors[errorIndex]);
				}
			}
		}
	}
	
	private static void retropropagation(int classe, double error){
		// TODO
	}
	
	private static double[] applyNetwork(double[] data){
		return new double[]{};
	}
	
	private static void evaluation(){
		int correctClass = 0;
		int total = 0;
		for(int klass = 0; klass < nbClasses; klass++){
			for(int exIndex = nbExApprent; exIndex < nbEx; exIndex++){
				double max = Double.MIN_VALUE;
				int classe = -1;
				double[] output = applyNetwork(data[klass][exIndex]);
				for(int layerIndex = 0; layerIndex < output.length; layerIndex++){
					if(output[layerIndex] > max){
						max = output[layerIndex];
						classe = layerIndex;
					}
				}
				LOGGER.debug("Classe {} - classe trouvée {}", klass, classe);
				if(klass == classe){
					correctClass++;
				}
				total++;
			}
		}
		LOGGER.info("Taux de reconnaissance : {}", correctClass * 100.0 / total);
	}
	
	private static void readFile(Path path){
		int classe = 0;
		int n = 0;
		try{
			for(String line : Files.readAllLines(path)){
				for(int caracIndex = 0; caracIndex < nbCaract; caracIndex++){
					String valCarac = line.substring(caracIndex * nbCaract, caracIndex * nbCaract + 3);
					data[classe][n][caracIndex] = Double.parseDouble(valCarac);
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
