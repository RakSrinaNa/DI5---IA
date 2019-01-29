package fr.mrcraftcod.ia;

import java.io.*;

/**
 * @author hubert.cardot
 */
public class Main{  // pg du MLP, r�seau de neurones � r�tropropagation
	
	private static int nbClasses = 3, nbCaract = 4, nbEx = 50, nbExApprent = 25;
	private static int nbCouches = 3, nbCaches = 6, nbApprent = 2000;
	private static int nbNeurones[] = {
			nbCaract + 1,
			nbCaches + 1,
			nbClasses
	}; //+1 pour neurone fixe
	private static Double[][][] data = new Double[nbClasses][nbEx][nbCaract];
	private static Double[][][] poids;
	private static Double[][] N;
	private static Double[][] S;
	private static Double coeffApprent = 0.01;
	private static Double coeffSigmoide = 2.0 / 3;
	
	private static Double fSigmoide(Double x){       // f()
		return Math.tanh(coeffSigmoide * x);
	}
	
	private static Double dfSigmoide(Double x){       // df()
		return coeffSigmoide / Math.pow(Math.cosh(coeffSigmoide * x), 2);
	}
	
	public static void main(String[] args){
		System.out.println("Caches=" + nbCaches + " App=" + nbApprent + " coef=" + coeffApprent);
		initialisation();
		apprentissage();
		evaluation();
	}
	
	private static void initialisation(){
		lectureFichier();
		//Allocation et initialisation al�atoire des poids
		poids = new Double[nbCouches - 1][][];
		for(int couche = 0; couche < nbCouches - 1; couche++){
			poids[couche] = new Double[nbNeurones[couche + 1]][];
			for(int i = 0; i < nbNeurones[couche + 1]; i++){
				poids[couche][i] = new Double[nbNeurones[couche]];
				for(int j = 0; j < nbNeurones[couche]; j++){
					poids[couche][i][j] = (Math.random() - 0.5) / 10; //dans [-0,05; +0,05[
				}
			}
		}
		//Allocation des �tats internes N et des sorties S des neurones
		N = new Double[nbCouches][];
		S = new Double[nbCouches][];
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
	
	private static void propagation(Double X[]){
		//---------- � faire
	}
	
	private static void retropropagation(int classe){
		//---------- � faire
	}
	
	private static void lectureFichier(){
		// lecture des donn�es � partir du fichier iris.data
		String ligne, sousChaine;
		int classe = 0, n = 0;
		try{
			BufferedReader fic = new BufferedReader(new FileReader("iris.data"));
			while((ligne = fic.readLine()) != null){
				for(int i = 0; i < nbCaract; i++){
					sousChaine = ligne.substring(i * nbCaract, i * nbCaract + 3);
					data[classe][n][i] = Double.parseDouble(sousChaine);
					//System.out.println(data[classe][n][i]+" "+classe+" "+n);
				}
				if(++n == nbEx){
					n = 0;
					classe++;
				}
			}
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
	}
}  //------------------fin classe Main--------------------
