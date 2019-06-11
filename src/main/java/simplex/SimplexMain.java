package simplex;

import java.util.ArrayList;
import java.util.List;

public class SimplexMain {

	public static void main( String[] args ) {
		
		try {
			
			//Integer size = Integer.parseInt( args[0] );
			Integer size = 3;
			
			Integer M[][] = new Integer[size - 1][size - 1];
			
			List< List< String > > diagonals = new ArrayList< List< String > >();
			
			rightDiagonals( M, size, diagonals );
			leftDiagonals( M, size, diagonals );

			printModel( size, diagonals );
			
		} catch( Exception ex ) {
			ex.printStackTrace();
		}
		
	}
	
	public static void printModel( Integer size, List< List< String > > diagonals ) {
		
		/**
		 * Z-Function
		 */
				
		String zFunction = "";
		for( int i = 1 ; i <= size ; i++ ) {
			for( int j = 1 ; j <= size ; j++ ) {
				String variable = "x" + i + "" + j;
				zFunction = zFunction.concat( variable );
				zFunction = zFunction.concat( " + " );
			}
		}
		
		System.out.print( "max z = " + zFunction.substring(0, zFunction.length() - 2) );

		System.out.println( "" );
		System.out.println( "s.a" );
		
		/**
		 * Line restriction
		 */
		for( int i = 1 ; i <= size ; i++ ) {
			System.out.println( "" );
			for( int j = 1 ; j <= size ; j++ ) {
				String variable = "x" + i + "" + j;
				System.out.print( variable );
				if( j < size ) {
					System.out.print( " + " );
				}
			}
			System.out.print( " <= 1" );
		}
		
		/**
		 * Column restriction
		 */
		for( int i = 1 ; i <= size ; i++ ) {
			System.out.println( "" );
			for( int j = 1 ; j <= size ; j++ ) {
				String variable = "x" + j + "" + i;
				System.out.print( variable );
				if( j < size ) {
					System.out.print( " + " );
				}
			}
			System.out.print( " <= 1" );
		}
		
		/**
		 * Diagonal restriction
		 */
		for( List< String > list : diagonals ) {
			System.out.println( "" );
			for( int i = 0 ; i < list.size() ; i++ ) {
				System.out.print( "x" + list.get( i ) );
				if( i < list.size() - 1 ) {
					System.out.print( " + " );
				}
			}
			System.out.print( " <= 1 " );
		}
		
		/**
		 * Positive restriction
		 */
		System.out.println( "");
		System.out.println( "xij, 1 <= i <= " + size + ", 1 <= j <= " + size + " E { 0, 1 }");
		
	}
	
	public static void leftDiagonals( Integer M[][], Integer size, List< List< String > > diagonals ) {
		/**
		 * Column iteraction
		 */
		for( int i = 1 ; i <= size ; i++ ) {
			Integer tmpLine = i;
			Integer tmpColumn = size;
			List< String > currentDiagonal = new ArrayList< String >();
			while( tmpLine <= size && tmpColumn <= size ) {
				currentDiagonal.add( tmpLine + "" + tmpColumn );
				tmpLine++;
				tmpColumn--;
			}
			diagonals.add( currentDiagonal );
		}
		
		/**
		 * Line iteraction
		 */
		for( int i = size - 1 ; i >= 1 ; i-- ) {
			Integer tmpLine = 1;
			Integer tmpColumn = i;
			List< String > currentDiagonal = new ArrayList< String >();
			while( tmpLine <= size && tmpColumn >= 1 ) {
				currentDiagonal.add( tmpLine + "" + tmpColumn );
				tmpLine++;
				tmpColumn--;
			}
			diagonals.add( currentDiagonal );
		}
	}
	
	public static void rightDiagonals( Integer M[][], Integer size, List< List< String > > diagonals ) {
		
		/**
		 * Column iteraction
		 */
		for( int i = 1 ; i <= size ; i++ ) {
			Integer tmpLine = i;
			Integer tmpColumn = 1;
			List< String > currentDiagonal = new ArrayList< String >();
			while( tmpLine <= size && tmpLine <= size ) {
				currentDiagonal.add( tmpLine + "" + tmpColumn );
				tmpLine++;
				tmpColumn++;
			}
			diagonals.add( currentDiagonal );
		}
		
		/**
		 * Line iteraction
		 */
		for( int i = 2 ; i <= size ; i++ ) {
			Integer tmpLine = 1;
			Integer tmpColumn = i;
			List< String > currentDiagonal = new ArrayList< String >();
			while( tmpLine <= size && tmpColumn <= size ) {
				currentDiagonal.add( tmpLine + "" + tmpColumn );
				tmpLine++;
				tmpColumn++;
			}
			diagonals.add( currentDiagonal );
		}
	}
	
}
