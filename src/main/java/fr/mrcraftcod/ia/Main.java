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
		for(int apprent = 0; apprent < nbApprent; apprent++){
			for(int klass = 0; klass < nbClasses; klass++){
				for(int apprentIndex = 0; apprentIndex < nbExApprent; apprentIndex++){
					LOGGER.debug("Apprent time={} class={}, index={}", apprent, klass, apprentIndex);
					double[] results = applyNetwork(data[klass][apprentIndex]);
					for(int i = 0; i < results.length; i++){
						retropropagation(i, i == klass ? 1 : 0);
					}
				}
			}
		}
	}
	
	private static void retropropagation(int classe, double desiredOutput){
		double[][] deltas = new double[nbCouches][];
		for(int i = 0; i < nbCouches; i++){
			deltas[i] = new double[nbNeurones[i]];
		}
		
		// TODO: Couche de sortie: delta_i = (Si-Di) * f'(Ni)
		var exitLayer = nbCouches - 1;
		for(int neuroneIndex = 0; neuroneIndex < nbNeurones[exitLayer]; neuroneIndex++){
			deltas[exitLayer][neuroneIndex] = classe == neuroneIndex ? ((S[exitLayer][neuroneIndex] - desiredOutput) * dfSigmoide(N[exitLayer][neuroneIndex])) : 0;
		}
		
		// TODO: Autre couche: delta_i = sum_k(delta_k * w_ki) * f'(Ni)
		for(var fromLayer = nbCouches - 2; fromLayer >= 0; fromLayer--){
			for(int neuroneFrom = 0; neuroneFrom < nbNeurones[fromLayer]; neuroneFrom++){
				var sum = 0D;
				for(int neuroneTo = 0; neuroneTo < nbNeurones[fromLayer + 1]; neuroneTo++){
					sum += deltas[fromLayer + 1][neuroneTo] * poids[fromLayer][neuroneTo][neuroneFrom];
				}
				deltas[fromLayer][neuroneFrom] = sum * dfSigmoide(N[fromLayer][neuroneFrom]);
			}
		}
		
		// TODO: DELTA=-mu*delta_i*Sj
		for(int layerFrom = 0; layerFrom < nbCouches - 1; layerFrom++){
			for(int neuroneTo = 0; neuroneTo < nbNeurones[layerFrom + 1]; neuroneTo++){
				for(int neuroneFrom = 0; neuroneFrom < nbNeurones[layerFrom]; neuroneFrom++){
					poids[layerFrom][neuroneTo][neuroneFrom] += -coeffApprent * deltas[layerFrom + 1][neuroneTo] * S[layerFrom][neuroneFrom];
				}
			}
		}
	}
	
	private static void evaluation(){
		int correctClass = 0;
		int total = 0;
		int[][] counts = new int[nbClasses][];
		for(int i = 0; i < counts.length; i++){
			counts[i] = new int[nbClasses];
		}
		for(int klass = 0; klass < nbClasses; klass++){
			for(int exIndex = nbExApprent; exIndex < nbEx; exIndex++){
				double max = -Double.MAX_VALUE;
				int classe = -1;
				double[] output = applyNetwork(data[klass][exIndex]);
				for(int layerIndex = 0; layerIndex < output.length; layerIndex++){
					if(output[layerIndex] > max){
						max = output[layerIndex];
						classe = layerIndex;
					}
				}
				counts[klass][classe] += 1;
				if(klass == classe){
					LOGGER.debug("Classe {} - classe trouvée {}", klass, classe);
					correctClass++;
				}
				else{
					LOGGER.warn("Classe {} - classe trouvée {}", klass, classe);
				}
				total++;
			}
		}
		LOGGER.info("Taux de reconnaissance : {}%", correctClass * 100.0 / total);
		for(int[] count : counts){
			LOGGER.info("{}", count);
		}
	}
	
	private static double[] applyNetwork(double[] data){
		/* Init first nodes */
		N[0][0] = 1;
		System.arraycopy(data, 0, N[0], 1, data.length);
		for(int i = 0; i < N[0].length; i++){
			S[0][i] = fSigmoide(N[0][i]);
		}
		
		/* Propagate N & S values to other layers */
		for(int layerTo = 1; layerTo < nbCouches; layerTo++){
			if(layerTo != nbCouches - 1){
				N[layerTo - 1][0] = 1;
			}
			for(int neuroneTo = (layerTo == nbCouches - 1) ? 0 : 1; neuroneTo < nbNeurones[layerTo]; neuroneTo++){
				var n = 0D;
				for(int neuroneFrom = 0; neuroneFrom < nbNeurones[layerTo - 1]; neuroneFrom++){
					n += poids[layerTo - 1][neuroneTo][neuroneFrom] * S[layerTo - 1][neuroneFrom];
				}
				N[layerTo][neuroneTo] = n;
				S[layerTo][neuroneTo] = fSigmoide(n);
			}
		}
		
		return S[nbCouches - 1];
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
