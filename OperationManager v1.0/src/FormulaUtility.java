package src;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class FormulaUtility {
   public static final String[] OPERATIONS = {"^", "*", "/", "+","-"};
   public static final String[] OPEN_SEPARATORS = {"("};
   public static final String[] CLOSED_SEPARATORS = {")"};
   public static final String[] VARIABLES = {"x", "y"};
   public static final String[] SPECIAL_OPERATIONS = {"ln", "log", "sin", "cos", "tan"};
   public static final String[] SPECIAL_OPERATION_STARTERS = {"l", "s", "c", "t"};

   public static double solve (String g, double x, double y) {return 0;}

   public enum Type {
      NUM ("number"), OP ("operation"), VAR ("variable"), O_SEP ("open separator"), C_SEP ("closed separator"), S_OP ("special operation"), TEMP ("temporary"), PH ("place holder");
      private final String t;
      private Type (String c) {t = c;}
      public String toString () {return t;}
   }

   public enum Variable {
      X ("x"), Y ("y");
      private final String t;
      private Variable (String c) {t = c;}
      public String toString () {return t;}
   }

   public enum Operation {
      ADD ("+"), SUB ("-"), DIV ("/"), MULT ("*"), EXP ("^");
      public final static int numOfOrders = 3;
      private final String t;
      private Operation (String c) {t = c;}
      public int getOrder () {
         if (this == EXP) {return 1;}
         else if (this == MULT || this == DIV) {return 2;}
         else {return 3;}
      }
      public String toString () {return t;}
   }

   public enum SpecialOperation {
      LN ("ln"), LOG ("log"), SIN ("sin"), COS ("cos"), TAN ("tan");
      private final String t;
      private SpecialOperation (String c) {t = c;}

      public String toString () {return t;}
   }

   public enum Separator {
      OPEN ("("), CLOSE (")");
      String g;
      private Separator (String t) {g = t;}
      public String toString () {return g;}
   }

   public static class TypeWrapper {
      private int index;
      private Type type;

      public TypeWrapper (int index, Type type) {
         this.index = index;
         this.type = type;
      }

      public Type getType() {return type;}
      public int getIndex() {return index;}

      public String toString() {
         return type.toString();
      }
   }

   public static class ExtractionWrapper {
      private Formula newFormula;
      private ArrayList<Formula> extractedFormulas;

      public ExtractionWrapper (Formula nF, ArrayList<Formula> eF) {
         newFormula = nF;
         extractedFormulas = eF;
      }

      public Formula getNewFormula() {return newFormula;}
      public ArrayList<Formula> getExtractedFormulas() {return extractedFormulas;}
   }

   public static class SeparatorPair {
      private int initialIndex;
      private int finalIndex;
      public SeparatorPair (int start, int end) {
         this.initialIndex = start;
         this.finalIndex = end;
      }
      public SeparatorPair (int row) {
         this.initialIndex = row;
         this.finalIndex = -1;
      }
   
      public int getInitialIndex () {return initialIndex;}
      public int getFinalIndex () {return finalIndex;}
      public void setIniialIndex (int i) {initialIndex = i;}
      public void setFinalIndex (int i) {finalIndex = i;}
   
      @Override
      public String toString () {
         return "[" + initialIndex + "," + finalIndex + "]";
      }
   }

   public static Type toType (String g) {
      for (String j : OPERATIONS) {
         if (g.equals(j)) return Type.OP;
      }

      for (String j : VARIABLES) {
         if (g.equals(j)) return Type.VAR;
      }

      for (String j : OPEN_SEPARATORS) {
         if (g.equals(j)) return Type.O_SEP;
      }
      
      for (String j : CLOSED_SEPARATORS) {
         if (g.equals(j)) return Type.C_SEP;
      }

      for (String j : SPECIAL_OPERATION_STARTERS) {
         if (g.equals(j)) return Type.S_OP;
      }

      if (g.equals("$")) return Type.PH;

      try {
         Integer.parseInt(g);
         return Type.NUM;
      }
      catch (NumberFormatException e) {
         throw new InvalidParameterException("Incorrect numerical value in input.");
      }
   }

   public static Variable toVariable (String g) {
      switch (g) {
         case "x" : return Variable.X;
         case "y" : return Variable.Y;
         default : throw new InvalidParameterException("Incorrect numerical value in input.");
      }
   }

   public static Operation toOperation (String g) {
      switch (g) {
         case "^" :  return Operation.EXP;
         case "*" :  return Operation.MULT;
         case "/" :  return Operation.DIV;
         case "+" :  return Operation.ADD;
         case "-" :  return Operation.SUB;
         default : throw new InvalidParameterException("Incorrect numerical value in input.");
      }
   }

   public static SpecialOperation toSpecialOperation (String g) {
      switch (g) {
         case "ln" :  return SpecialOperation.LN;
         case "log" :  return SpecialOperation.LOG;
         case "sin" :  return SpecialOperation.SIN;
         case "cos" :  return SpecialOperation.COS;
         case "tan" :  return SpecialOperation.TAN;
         default : throw new InvalidParameterException("Incorrect numerical value in input.");
      }
   }

   public static double extractDouble (String c, int initialIndex) {
      int i = initialIndex;
      String tAns = "";
      while (i < c.length()) {
         try {
            tAns += Integer.parseInt(c.substring(i, i+1));
         }
         catch (NumberFormatException e) {
            if (c.substring(i, i+1).equals(".")) tAns += ".";
            else break;
         }
         i++;
      }
      try {return Double.parseDouble(tAns);}
      catch (NumberFormatException e) {throw new InvalidParameterException("Incorrect numerical value in input.");}
   }

   public static SpecialOperation extractSpecialOperation (String c, int initialIndex) {

      final int maxLength = 3;
      final int minLength = 2;
      if ((c.length() - initialIndex) < minLength) throw new InvalidParameterException("Incorrect numerical value in input.");
      for (int i = minLength; i <= maxLength; i++) {
         try {
            return toSpecialOperation(c.substring(initialIndex, initialIndex + i));
         }
         catch (InvalidParameterException e) {;}
      }
      throw new InvalidParameterException("Incorrect numerical value in input.");  
   }

   public static double biCalculations (Operation z, double x, double y) {
      switch (z) {
         case ADD :
            return x + y;
         case SUB :
            return x - y;
         case DIV :
            return x / y;
         case MULT :
            return x * y;
         case EXP :
            return Math.pow(x,y);
         default :
            throw new InvalidParameterException("Incorrect numerical value in input.");
      }
   }

   public static double specialCalculations (SpecialOperation z, double x) {
      switch (z) {
         case SIN :
            return Math.sin(x);
         case COS :
            return Math.cos(x);
         case LOG :
            return Math.log10(x);
         case LN :
            return Math.log(x);
         default :
            throw new InvalidParameterException("Incorrect numerical value in input.");
      }
   }
}