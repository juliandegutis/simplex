package montecarlo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MonteCarlo {

	private static String report;
	
	public static void main( String[] args ) {
		
		try {
			
			Integer size = Integer.parseInt( args[0] );
			Integer qntAttempt = Integer.parseInt( args[1] );
			long startTime = System.nanoTime();
			long beforeUsedMem = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

			report = "Iniciando o processo da otimização do problema das rainhas em um tabuleiro de xadrez " + size + "x" + size + "\n\n"; 
			
			
			Integer M[][] = new Integer[size][size];
			
			List< List< Spot > > diagonals = new ArrayList< List< Spot > >();
			
			rightDiagonals( size, diagonals );
			leftDiagonals( size, diagonals );	
			
			monteCarloAlgorithm( M, size, diagonals, qntAttempt );
			
			long endTime = System.nanoTime();
			long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

			report = report.concat( "Tempo de execução: " + ( ( endTime - startTime ) / 1000000 ) + " milisegundos. \n" );
			report = report.concat( "Aproximação de quantidade de memória gasta: " + ( beforeUsedMem - afterUsedMem ) + " bytes. \n" );
			
			FileWriter writer = new FileWriter( "report/MONTE_CARLO" + size + "x" + size +"_" + System.currentTimeMillis() + ".txt" );
		    BufferedWriter bw = new BufferedWriter( writer );

		    bw.write( report );
		    
		    bw.close();

		} catch( Exception ex ) {
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * Fills the matrix with zeros
	 * 
	 * @param M
	 * @param size
	 */
	public static void fill( Integer M[][], Integer size ) {
		for( int i = 0 ; i < size ; i++ ) {
			for( int j = 0 ; j < size ; j++ ) {
				M[i][j] = 0;
			}
		}
	}
	
	/**
	 * 
	 * Process the Monte Carlo Algorithm 
	 * 
	 * @param M
	 * @param size
	 * @param diagonals
	 * @param qntAttempt
	 */
	public static void monteCarloAlgorithm( Integer M[][], Integer size, List< List< Spot > > diagonals, Integer qntAttempt ) {
		
		/**
		 * Best optimum of all iterations
		 */
		Integer finalOptimum = 0;
		List< Spot > solution = new ArrayList< Spot >();
		
		for( int i = 0 ; i < qntAttempt ; i++ ) {		
			
			/**
			 * Reset the matrix with zeros
			 */
			fill( M, size );
			
			List< Spot > spotsTaken = new ArrayList< Spot >();
			List< Spot > validSpots = new ArrayList< Spot >();
			Integer optimum;
			/**
			 * Take a random position and set into the matrix
			 */
			Integer x = new Random().nextInt( size );
			Integer y = new Random().nextInt( size );
			spotsTaken.add( new Spot( x, y ) );		
			validSpots.add( new Spot( x, y ) );
			M[x][y] = 1;
			optimum = 1;
		
			for(;;) {
				Integer tmpX = new Random().nextInt( size );
				Integer tmpY = new Random().nextInt( size );
				Spot tmpSpot = new Spot( tmpX, tmpY );
				/**
				 * Checks if the spot is already taken by any other queen
				 */
				if( !spotsTaken.contains( tmpSpot ) ) {
					spotsTaken.add( tmpSpot );
					/**
					 * Puts the queen in the random spot
					 */
					M[tmpX][tmpY] = 1;
					
					/**
					 * Checks if its break the problem rule
					 * Rule:
					 * If breaks -> Take the queen of the temporary spot
					 * If doesn't -> Increments the optimum
					 */
					if( !validate( M, size, diagonals ) ) {
						M[tmpX][tmpY] = 0;
					} else {
						validSpots.add( new Spot( tmpX, tmpY ) );
						optimum++;
					}
				}
				/**
				 * Do it until all spots are tried
				 */
				if( spotsTaken.size() == size * size  ) {
					break;
				}
			}
			
			/**
			 * Prints the optimum for current iteration
			 */
			report = report.concat( "Ótimo encontrado para iteração de número " + i + ": " + optimum + "\n" );
			
			/**
			 * Checks if it is the best optimum
			 */
			if( finalOptimum < optimum ) {
				solution = validSpots;
				finalOptimum = optimum;
			}
			
		}
		
		report = report.concat( "\n" );
		
		report = report.concat( "Ótimo após " + qntAttempt + " interações: " + finalOptimum + "\n\n" );
		report = report.concat( "Solução:\n" );
		for( Spot spot : solution ) {
			report = report.concat( "Ponto:" + ( spot.getX() + 1 ) + "," + ( spot.getY() + 1 ) + "\n" );
		}
		
	}
	
	/**
	 * 
	 * Validates the queen rules for this problem
	 * 
	 * @param M
	 * @param size
	 * @param diagonals
	 * @return False if the rule is broke / True if the rules continues
	 */
	public static Boolean validate( Integer M[][], Integer size, List< List< Spot > > diagonals ) {
		
		/**
		 * Checking line/column
		 */
		for( int i = 0 ; i < size ; i++ ) {
			Integer validatorColumn = 0;
			Integer validatorLine = 0;
			for( int j = 0 ; j < size ; j++ ) {
				validatorColumn = validatorColumn + M[i][j];
				validatorLine = validatorLine + M[j][i];
				if( validatorColumn > 1 || validatorLine > 1 ) {
					return Boolean.FALSE;
				}
			}
		}
		
		/**
		 * Checking diagonals
		 */
		for( List< Spot > spots : diagonals ) {
			Integer validatorDiagonal = 0;
			for( Spot spot : spots ) {
				validatorDiagonal = validatorDiagonal + M[spot.getX()][spot.getY()];
				if( validatorDiagonal > 1 ) {
					return Boolean.FALSE;
				}
			}
		}
		
		return Boolean.TRUE;
	}
	
	/**
	 * 
	 * Find all left oriented diagonais from a N (as size) dimensional matrix
	 * 
	 * @param M
	 * @param size
	 * @param diagonals
	 */
	public static void leftDiagonals( Integer size, List< List< Spot > > diagonals ) {
		/**
		 * Column iteraction
		 */
		for( int i = 1 ; i <= size ; i++ ) {
			Integer tmpLine = i;
			Integer tmpColumn = size;
			List< Spot > currentDiagonal = new ArrayList< Spot >();
			while( tmpLine <= size && tmpColumn <= size ) {
				currentDiagonal.add( new Spot( tmpLine - 1, tmpColumn - 1 ) );
				tmpLine++;
				tmpColumn--;
			}
			if( currentDiagonal.size() > 1 ) {
				diagonals.add( currentDiagonal );
			}
		}
		
		/**
		 * Line iteraction
		 */
		for( int i = size - 1 ; i >= 1 ; i-- ) {
			Integer tmpLine = 1;
			Integer tmpColumn = i;
			List< Spot > currentDiagonal = new ArrayList< Spot >();
			while( tmpLine <= size && tmpColumn >= 1 ) {
				currentDiagonal.add( new Spot( tmpLine - 1, tmpColumn - 1 ) );
				tmpLine++;
				tmpColumn--;
			}
			if( currentDiagonal.size() > 1 ) {
				diagonals.add( currentDiagonal );
			}
		}
	}
	
	/**
	 * 
	 * Find all right oriented diagonals from a N (as size) dimensional matrix
	 * 
	 * @param M
	 * @param size
	 * @param diagonals
	 */
	public static void rightDiagonals( Integer size, List< List< Spot > > diagonals ) {
		
		/**
		 * Column iteraction
		 */
		for( int i = 1 ; i <= size ; i++ ) {
			Integer tmpLine = i;
			Integer tmpColumn = 1;
			List< Spot > currentDiagonal = new ArrayList< Spot >();
			while( tmpLine <= size && tmpLine <= size ) {
				currentDiagonal.add( new Spot( tmpLine - 1, tmpColumn - 1 ) );
				tmpLine++;
				tmpColumn++;
			}
			if( currentDiagonal.size() > 1 ) {
				diagonals.add( currentDiagonal );
			}
		}
		
		/**
		 * Line iteraction
		 */
		for( int i = 2 ; i <= size ; i++ ) {
			Integer tmpLine = 1;
			Integer tmpColumn = i;
			List< Spot > currentDiagonal = new ArrayList< Spot >();
			while( tmpLine <= size && tmpColumn <= size ) {
				currentDiagonal.add( new Spot( tmpLine - 1, tmpColumn - 1 ) );
				tmpLine++;
				tmpColumn++;
			}
			if( currentDiagonal.size() > 1 ) {
				diagonals.add( currentDiagonal );
			}
		}
	}
	
}
