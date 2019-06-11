package simplex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.linear.PivotSelectionRule;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import scpsolver.constraints.LinearSmallerThanEqualsConstraint;
import scpsolver.lpsolver.LinearProgramSolver;
import scpsolver.lpsolver.SolverFactory;
import scpsolver.problems.LinearProgram;

public class SimplexMain {

	public static void main( String[] args ) {
		
		try {
			
			//Integer size = Integer.parseInt( args[0] );
			Integer size = 10;
			
			/**
			 * Object to map the M[�][j] point in a unidimensional matrix
			 */
			Map< String, Integer > mapPosition = new HashMap< String, Integer >();
			fill( mapPosition, size );
			
			Integer M[][] = new Integer[size - 1][size - 1];
			
			List< List< String > > diagonals = new ArrayList< List< String > >();
			
			rightDiagonals( M, size, diagonals );
			leftDiagonals( M, size, diagonals );

			Tableau tableau = buildModel( size, diagonals, mapPosition );
			
			optimizeBool( tableau, size );
			
		} catch( Exception ex ) {
			ex.printStackTrace();
		}
		
	}
	
	public static void fill( Map< String, Integer > mapPosition, Integer size ) {
		
		Integer pos = 0;
		for( int i = 1 ; i <= size ; i++ ) {
			for( int j = 1 ; j <= size ; j++ ) {
				String variable = "x" + i + "" + j;
				mapPosition.put( variable, pos );
				pos++;
			}
		}
		
	}
	
	public static void optimizeBool( Tableau tableau, Integer size ) {
		
		try {
			
			LinearProgram lp = new LinearProgram( tableau.getZFunction() );
			
			for( int i = 0 ; i < size * size ; i++ ) {
				lp.setBinary( i );
			}
			
			lp.setMinProblem( false );
			
			Integer identifier = 1;
			for( double[] restriction : tableau.getRestrictions() ) {
				lp.addConstraint(new LinearSmallerThanEqualsConstraint( restriction, 1.0, (identifier++).toString() ) ); 
			}
			
			LinearProgramSolver solver  = SolverFactory.newDefault(); 
			double[] sol = solver.solve( lp );
			
			for( int i = 0 ; i < sol.length ; i++ ) {
				System.out.println( ( i + 1 ) + " - " + sol[i] );
			}
			
		} catch( Exception ex ) {
			ex.printStackTrace();
		}
		
		
	}
	
	public static void optimizeReal( Tableau tableau, Integer size ) {
		
		try {
			
			LinearObjectiveFunction zFunction = new LinearObjectiveFunction( tableau.getZFunction(), 0 );
			ArrayList< LinearConstraint > constraints = new ArrayList<LinearConstraint>();
			for( double[] restriction : tableau.getRestrictions() ) {
				constraints.add( new LinearConstraint( restriction, Relationship.LEQ, 1 ) );
			}
			
			SimplexSolver solver = new SimplexSolver();
			
		    PointValuePair solution = solver.optimize(zFunction, new LinearConstraintSet(constraints),
		            GoalType.MAXIMIZE,
		            new NonNegativeConstraint(true),
		            PivotSelectionRule.BLAND);
		    			
		} catch( Exception ex ) {
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * Build and print the current model 
	 * 
	 * @param size
	 * @param diagonals
	 */
	public static Tableau buildModel( Integer size, List< List< String > > diagonals, Map< String, Integer > mapPosition ) {
		
		Tableau tableau = new Tableau();
		
		/**
		 * Z-Function
		 */		
		String zFunction = "";
		double[] zCoefs = new double[(size * size)];
		int currZ = 0;
		for( int i = 1 ; i <= size ; i++ ) {
			for( int j = 1 ; j <= size ; j++ ) {
				String variable = "x" + i + "" + j;
				zFunction = zFunction.concat( variable );
				zFunction = zFunction.concat( " + " );
				zCoefs[currZ++] = 1;
			}
		}
		tableau.setZFunction( zCoefs );
		
		System.out.print( "max z = " + zFunction.substring(0, zFunction.length() - 2) );

		System.out.println( "" );
		System.out.println( "s.a" );
		
		/**
		 * Line restriction
		 */
		for( int i = 1 ; i <= size ; i++ ) {
			double[] restriction = new double[(size * size)];
			System.out.println( "" );
			for( int j = 1 ; j <= size ; j++ ) {
				String variable = "x" + i + "" + j;
				System.out.print( variable );
				if( j < size ) {
					System.out.print( " + " );
				}
				restriction[ mapPosition.get( variable ) ] = 1;
			}
			tableau.addRestriction( restriction );
			System.out.print( " <= 1" );
		}
		
		/**
		 * Column restriction
		 */
		for( int i = 1 ; i <= size ; i++ ) {
			double[] restriction = new double[(size * size)];
			System.out.println( "" );
			for( int j = 1 ; j <= size ; j++ ) {
				String variable = "x" + j + "" + i;
				System.out.print( variable );
				if( j < size ) {
					System.out.print( " + " );
				}
				restriction[ mapPosition.get( variable ) ] = 1;
			}
			tableau.addRestriction( restriction );
			System.out.print( " <= 1" );
		}
		
		/**
		 * Diagonal restriction
		 */
		for( List< String > list : diagonals ) {
			System.out.println( "" );
			double[] restriction = new double[(size * size)];
			for( int i = 0 ; i < list.size() ; i++ ) {
				System.out.print( "x" + list.get( i ) );
				if( i < list.size() - 1 ) {
					System.out.print( " + " );
				}
				restriction[ mapPosition.get( "x" + list.get( i ) ) ] = 1;
			}
			tableau.addRestriction( restriction );
			System.out.print( " <= 1 " );
		}
		
		/**
		 * Positive restriction
		 */
		System.out.println( "" );
		System.out.println( "xij, 1 <= i <= " + size + ", 1 <= j <= " + size + " E { 0, 1 }");
	
		return tableau;
		
	}
	
	/**
	 * 
	 * Find all left oriented diagonais from a N (as size) dimensional matrix
	 * 
	 * @param M
	 * @param size
	 * @param diagonals
	 */
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
			List< String > currentDiagonal = new ArrayList< String >();
			while( tmpLine <= size && tmpColumn >= 1 ) {
				currentDiagonal.add( tmpLine + "" + tmpColumn );
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
			List< String > currentDiagonal = new ArrayList< String >();
			while( tmpLine <= size && tmpColumn <= size ) {
				currentDiagonal.add( tmpLine + "" + tmpColumn );
				tmpLine++;
				tmpColumn++;
			}
			if( currentDiagonal.size() > 1 ) {
				diagonals.add( currentDiagonal );
			}
		}
	}
	
}
