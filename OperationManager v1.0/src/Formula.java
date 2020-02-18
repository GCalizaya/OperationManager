package src;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import src.FormulaUtility;
import src.FormulaUtility.TypeWrapper;
import src.FormulaUtility.Type;
import src.FormulaUtility.ExtractionWrapper;
import src.FormulaUtility.Operation;
import src.FormulaUtility.Variable;
import src.FormulaUtility.SpecialOperation;
import src.FormulaUtility.SeparatorPair;

/**
 * Create formulas and call methods to solve them.
 * @author  Alex Cabrera
 * @version v1.0
 * @since 17/02/2020 (DD/MM/YY)
 */
public class Formula {
   private final TypeWrapper [] formula;
   private final Double [] numbers;
   private final Operation [] operations;
   private final Variable [] variables;
   private final SpecialOperation [] specialOperations;
   private final SeparatorPair [] separators;

   /**
    * Construct Formula objects by passsing in valid String.
    * Valid String: Closed parentheses, doubles instead of ints (2.0 v. 2), spaces allowed b/w opereations and parentheses (not b/w numbers).
    * Suppoerted operations: +, -, *, /, ^, sin, cos, tan, ln, log.
    * Passing in '$' as part of the String causes undefined behavior.
    * 
    * @param f: The String containing the desired formula
    */
   public Formula (String f) {
      ArrayList<TypeWrapper> mFunction = new ArrayList<TypeWrapper>(f.length()+2);
      ArrayList<Double> mNumbers = new ArrayList<Double>();
      ArrayList<Operation> mOperations = new ArrayList<Operation>();
      ArrayList<Variable> mVariables = new ArrayList<Variable>();
      ArrayList<SpecialOperation> mSpecialOperations = new ArrayList<SpecialOperation>();
      ArrayList <SeparatorPair> mSeparators = new ArrayList<SeparatorPair> ();

      Type superType = Type.TEMP;

      int superIterationModifier = 0;
      int numCounter = 0;
      int operationCounter = 0;
      int variableCounter = 0;
      int specialOperationCounter = 0;

      for (int i = 0; i < f.length(); i += (1 + superIterationModifier)) {

         superIterationModifier = 0;
         String sTemp = f.substring(i,i+1);

         if (sTemp.equals(" ")) continue;

         Type subType = FormulaUtility.toType(sTemp);

         switch (subType) {
            case OP :
               if (superType == Type.OP || superType == Type.S_OP || superType == Type.TEMP) throw new InvalidParameterException("Incorrect numerical value in input.");
               mOperations.add(FormulaUtility.toOperation(sTemp));
               mFunction.add(new TypeWrapper (operationCounter, subType));
               operationCounter ++;
               superType = subType;
               break;

            case VAR :
               if (superType == Type.VAR || superType == Type.NUM) throw new InvalidParameterException("Incorrect numerical value in input.");
               mVariables.add(FormulaUtility.toVariable(sTemp));
               mFunction.add(new TypeWrapper (variableCounter, subType));
               variableCounter ++;
               superType = subType;
               break;

            case O_SEP :
               if (superType == Type.VAR || superType == Type.NUM || superType == Type.C_SEP) throw new InvalidParameterException("Incorrect numerical value in input.");
               mSeparators.add(new SeparatorPair (mFunction.size()));
               mFunction.add(new TypeWrapper (-1, subType));
               superType = subType;
               break;

            case C_SEP :
               if (superType == Type.OP || superType == Type.S_OP || superType == Type.TEMP) throw new InvalidParameterException("Incorrect numerical value in input.");
               boolean finished = false;
               for (int u = mSeparators.size() - 1; u >= 0; u--) {
                  if (mSeparators.get(u).getFinalIndex() == -1) {
                     mSeparators.get(u).setFinalIndex(mFunction.size() + 1);
                     finished = true;
                     break;
                  }
               }
               if (!finished) throw new InvalidParameterException("Incorrect numerical value in input.");
               mFunction.add(new TypeWrapper (-1, subType));
               superType = subType;
               break;

            case S_OP :
               if (superType == Type.VAR || superType == Type.NUM || superType == Type.C_SEP) throw new InvalidParameterException("Incorrect numerical value in input.");
               SpecialOperation ans = FormulaUtility.extractSpecialOperation(f,i);
               superIterationModifier += ans.toString().length() - 1;
               mSpecialOperations.add(ans);
               mFunction.add(new TypeWrapper (specialOperationCounter, subType));
               specialOperationCounter ++;
               superType = subType;
               break;

            case NUM :
               if (superType == Type.VAR || superType == Type.C_SEP) throw new InvalidParameterException("Incorrect numerical value in input.");
               double value = FormulaUtility.extractDouble(f, i);
               superIterationModifier += Double.toString(value).length() - 1;
               mNumbers.add(value);
               mFunction.add(new TypeWrapper (numCounter, subType));
               numCounter ++;
               superType = subType;
               break;
            
            case PH :
               mFunction.add(new TypeWrapper (-1, subType));
               superType = subType;
               break;

            default : throw new InvalidParameterException("Incorrect numerical value in input.");
         }
      }

      if (superType == Type.OP || superType == Type.O_SEP || superType == Type.S_OP) throw new InvalidParameterException("Incorrect numerical value in input.");

      for (SeparatorPair k : mSeparators) {
         if (k.getFinalIndex() == -1) throw new InvalidParameterException("Incorrect numerical value in input.");
      }

      formula = new TypeWrapper [mFunction.size()];
      numbers = new Double [mNumbers.size()];
      variables = new Variable [mVariables.size()];
      operations = new Operation [mOperations.size()];
      specialOperations = new SpecialOperation [mSpecialOperations.size()];
      separators = new SeparatorPair [mSeparators.size()];

      mFunction.toArray(formula);
      mNumbers.toArray(numbers);
      mVariables.toArray(variables);
      mOperations.toArray(operations);
      mSpecialOperations.toArray(specialOperations);
      mSeparators.toArray(separators);
   }

   public TypeWrapper [] getFunction () {return formula;}
   public Double [] getNumbers () {return numbers;}
   public Variable [] getVariables () {return variables;}
   public Operation [] getOperations () {return operations;}
   public SpecialOperation [] getSpecialOperations () {return specialOperations;}
   public SeparatorPair [] getSeparators () {return separators;}

   public ArrayList<TypeWrapper> copyFunction () {
      ArrayList<TypeWrapper> ans = new ArrayList<TypeWrapper> ();
      for (TypeWrapper t : formula) ans.add(t);
      return ans;
   }

   public ArrayList<Double> copyNumbers () {
      ArrayList<Double> ans = new ArrayList<Double> ();
      for (double d : numbers) ans.add(d);
      return ans;
   }

   public ArrayList<Operation> copyOperations () {
      ArrayList<Operation> ans = new ArrayList<Operation> ();
      for (Operation d : operations) ans.add(d);
      return ans;
   }

   public ArrayList<SpecialOperation> copySpecialOperations () {
      ArrayList<SpecialOperation> ans = new ArrayList<SpecialOperation> ();
      for (SpecialOperation d : specialOperations) ans.add(d);
      return ans;
   }

   public ArrayList <SeparatorPair> copySeparators () {
      ArrayList<SeparatorPair> ans = new ArrayList<SeparatorPair> ();
      for (SeparatorPair t : separators) ans.add(t);
      return ans;
   }

   public Formula mergeFormulas (Formula... a) {
      Formula [] t = a;
      String g = "";
      for (Formula i : t) {
         g += i;
      }
      return new Formula(g);
   }

   /**
    * Gets the innermost parentheses pairs that the functions contains, 
    * and returns a SeparatorPair array (initial and final indeces of parenetheses).
    */

   private ArrayList<SeparatorPair> getInnerOperations() {
      ArrayList<SeparatorPair> ans = new ArrayList<SeparatorPair>();
      outer:
      for (SeparatorPair i : separators) {
         for (SeparatorPair j : separators) {
            if (i.getInitialIndex() < j.getInitialIndex() && i.getFinalIndex() > j.getFinalIndex()) continue outer;
         }
         ans.add(i);
      }
      return ans;
   }

   /**
    * Using the getInnerOperationsMethod, extracts the innermost formulas that the 
    * current formula has, and replaces them (as a new object) with place holder values (PH).
    * @return ExtractionWrapper, a container for the original formula with extracted operations, 
    * and an array of the extracted operations themselves.
    */

   private ExtractionWrapper extractInnerOperations () {
      ArrayList<SeparatorPair> mPairs = getInnerOperations();
      ArrayList<Formula> extractedFormulas = new ArrayList<Formula>();
      String modifiedFormula = "";
      int startIntegration = 0;

      for (SeparatorPair t : mPairs) {
         extractedFormulas.add(new Formula (toRangeString(t.getInitialIndex()+1, t.getFinalIndex()-1)));
         modifiedFormula += toRangeString (startIntegration, t.getInitialIndex()) + "$";
         startIntegration = t.getFinalIndex();
      }
      modifiedFormula += toRangeString (startIntegration, formula.length);

      return new ExtractionWrapper (new Formula (modifiedFormula), extractedFormulas);
   }

   /**
    * Using the a ExtractionWrapperObject (only obtained from the extractInnerOperations method)
    * solves the extracted innermost operation of the original formula and replaces them back in.
    * @param a: ExtractionWrapper from extracInnerOperations
    * @return Original formula containing solved innermost operations.
    */

   private static Formula solveExtractedEquations (ExtractionWrapper a) {
      ArrayList<Double> answers = new ArrayList <Double> (a.getExtractedFormulas().size());
      for (Formula t : a.getExtractedFormulas()) {
         answers.add(t.solveSimpleSpecialOperations().solveSimpleOperations());
      }
      Formula tFormula = a.getNewFormula();
      TypeWrapper [] tFunction = tFormula.getFunction();
      String tAns = "";
      int startIndex = 0;
      int position = 0;
      for (int i = 0; i < tFunction.length; i++) {
         TypeWrapper t = tFunction [i];
         if (t.getType() == Type.PH) {
            tAns += tFormula.toRangeString(startIndex, i);
            tAns += answers.get (position);
            i++;
            position++;
            startIndex = i;
         }
      }
      tAns += tFormula.toRangeString(startIndex, tFunction.length);
      return new Formula (tAns);
   }

   private Formula replaceInnerOperations () {
      return solveExtractedEquations(extractInnerOperations());
   }

   /**
    * Solves only the special operations of a simple formula.
    * @return Formula obejct with solved specialOperations 
    * (for examples, cos0 becomes 1)
    */

   private Formula solveSimpleSpecialOperations () {
      String ans = "";
      for (int i = 0; i < formula.length; i++) {
         Type currentType = formula[i].getType();
         int currentIndex = formula[i].getIndex();
         switch (currentType) {
            case S_OP :
               ans += FormulaUtility.specialCalculations(specialOperations[currentIndex], numbers[formula[i+1].getIndex()]);
               i++;
               break;
            case OP :
               ans += operations[currentIndex];
               break;
            case NUM :
               ans += numbers[currentIndex];
               break;
            default: throw new InvalidParameterException("Incorrect numerical value in input.");
         }
      }

      return new Formula(ans);
   }

   /**
    * Assuming all special operations have been previously solved, solves all simple
    * biValue operations in order of operation.
    * @return solved simple formula (double).
    */

   private double solveSimpleOperations () {
      String stringAns = "";
      Formula tFormula = new Formula (this.toString());
      TypeWrapper [] tFunction = tFormula.getFunction();
      int currentOrder = 1;
      while (currentOrder < Operation.numOfOrders) {
         double lastNumber = 0;
         for (int i = 0; i < tFunction.length; i++) {
            Type currentType = tFunction[i].getType();
            int currentIndex = tFunction[i].getIndex();
            outer :
            switch (currentType) {
               case OP :
                  if (tFormula.operations[currentIndex].getOrder() != currentOrder) {
                     stringAns += "" + lastNumber + tFormula.operations[currentIndex];
                     break outer;
                  }
                  lastNumber = FormulaUtility.biCalculations(tFormula.operations[currentIndex], lastNumber, tFormula.numbers[tFunction[i+1].getIndex()]);
                  i++;
                  break;
               case NUM :
                  lastNumber = tFormula.numbers[currentIndex];
                  break;
               default: throw new InvalidParameterException("Incorrect numerical value in input.");
            }
         }
         stringAns += lastNumber;
         tFormula = new Formula (stringAns);
         tFunction = tFormula.getFunction();
         stringAns = "";
         currentOrder++;
      }
      
      double doubleAns = tFormula.numbers[tFunction[0].getIndex()];
      for (int i = 1; i < tFunction.length; i++) {
         Type currentType = tFunction[i].getType();
         int currentIndex = tFunction[i].getIndex();
         switch (currentType) {
            case OP :
               doubleAns = FormulaUtility.biCalculations(tFormula.operations[currentIndex], doubleAns, tFormula.numbers[tFunction[i+1].getIndex()]);
               i++;
               break;
            case NUM :
               break;
            default: throw new InvalidParameterException("Incorrect numerical value in input.");
         }
      }

      return doubleAns;
   }

   public Formula replaceVariables (double x, double y){
      int startIndex = 0;
      String tAns = "";
      for (int i = 0; i < formula.length; i++) {
         TypeWrapper t = formula [i];
         if (t.getType() == Type.VAR) {
            tAns += this.toRangeString(startIndex, i);
            i++;
            startIndex = i;
            switch (variables[t.getIndex()]) {
               case X : 
                  tAns += x;
                  break;
               case Y :
                  tAns += y;
                  break;
            }
         }
      }
      tAns += this.toRangeString(startIndex, formula.length);
      return new Formula (tAns);
   }

   public double solve (double x, double y) {
      Formula tFormula = replaceVariables(x, y);
      ExtractionWrapper a = tFormula.extractInnerOperations();
      while (a.getExtractedFormulas().size() > 0) {
         tFormula = solveExtractedEquations(a);
         a = tFormula.extractInnerOperations();
      }

      return tFormula.solveSimpleSpecialOperations().solveSimpleOperations();
   }

   public double solve () {
      return solve (0,0);
   }

   public String toRangeString (int i, int f) {
      if ((i < 0 && i > formula.length) && (f < 0 && f > formula.length) && (i >= f)) throw new InvalidParameterException("Incorrect numerical value in input.");
      String ans = "";
      for (int j = i; j < f; j++) {
         Type currentType = formula[j].getType();
         int currentIndex = formula[j].getIndex();
         switch (currentType) {
            case NUM:
               ans += numbers[currentIndex];
               break;
            case VAR:
               ans += variables[currentIndex];
               break;
            case OP:
               ans += operations[currentIndex];
               break;
            case S_OP:
               ans += specialOperations[currentIndex];
               break;
            case O_SEP:
               ans += "(";
               break;
            case C_SEP:
               ans += ")";
               break;
            default:
         }
      }
      return ans;
   }

   public String toString () {
      String ans = "";
      for (TypeWrapper t : formula) {
         Type currentType = t.getType();
         int currentIndex = t.getIndex();
         switch (currentType) {
            case NUM:
               ans += numbers[currentIndex];
               break;
            case VAR:
               ans += variables[currentIndex];
               break;
            case OP:
               ans += operations[currentIndex];
               break;
            case S_OP:
               ans += specialOperations[currentIndex];
               break;
            case O_SEP:
               ans += "(";
               break;
            case C_SEP:
               ans += ")";
               break;
            default:
         }
      }
      return ans;
   }

   public String toInfoString () {
      return "- Real Formula: " + formula + "\n- Numbers: " + numbers + "\n- Operations: " + operations + "\n- Variables: " + variables + "\n- Special Operations: " + specialOperations + "\n- Separators: " + separators + "}";
   }
}