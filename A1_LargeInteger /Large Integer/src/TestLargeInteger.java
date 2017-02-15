import java.lang.Math;

/*********************************************************/
/* NAME:     Guokai Tang                                           */
/* STUDENT ID:    260685449                                     */
/*********************************************************/

/* This class stores and manipulates very large non-negative integer numbers 
   The digits of the number are stored in an array of bytes. */
class LargeInteger {
     // comment
    /* The digits of the number are stored in an array of bytes. 
       Each element of the array contains a value between 0 and 9. 
       By convention, digits[digits.length-1] correspond to units, 
       digits[digits.length-2] corresponds to tens, digits[digits.length-3] 
       corresponds to hundreds, etc. */

    byte digits[];

    /* Constructor that creates a new LargeInteger with n digits */
    public LargeInteger (int n) {
        digits= new byte[n];
    }

    /* Constructor that creates a new LargeInteger whose digits are those of the string provided */
    public LargeInteger (String s) {
        digits = new byte[s.length()]; /* Note on "length" of arrays and strings: Arrays can be seen 
                                          as a class having a member called length. Thus we can access 
                                          the length of digits by writing digits.length
                                          However, in the class String, length is a method, so to access 
                                          it we need to write s.length() */

        for (int i=0;i<s.length();i++) digits[i] = (byte)Character.digit(s.charAt(i),10);
        /* Here, we are using a static method of the Character class, called digit, which 
           translates a character into an integer (in base 10). This integer needs to be 
           cast into a byte. ****/
    }

    /* Constructor that creates a LargeInteger from an array of bytes. Only the bytes  
       between start and up to but not including stop are copied. */
    public LargeInteger (byte[] array, int start, int stop) {
        digits = new byte[stop-start];
        for (int i=0;i<stop-start;i++) digits[i] = array[i+start];
    }


    /* This method returns a LargeInteger where eventual leading zeros are removed. 
       For example, it turns 000123 into 123. Special case: it turns 0000 into 0. */
    public LargeInteger removeLeadingZeros() {
        if (digits[0]!=0) return this;
        int i = 1;
        while (i<digits.length && digits[i]==0) i++;
        if (i==digits.length) return new LargeInteger("0");
        else return new LargeInteger(digits,i,digits.length);
    } // end of removeLeadingZeros


    /* This methods multiplies a given LargeInteger by 10^nbDigits, simply by shifting 
       the digits to the left and adding nbDigits zeros at the end */
    public LargeInteger shiftLeft(int nbDigits) {
        LargeInteger ret = new LargeInteger( digits.length + nbDigits );
        for (int i = 0 ; i < digits.length ; i++) ret.digits[ i ] = digits[ i ];
        for (int i = 0; i <  nbDigits; i++) ret.digits[ digits.length + i ] = 0;
        return ret;
    } // end of shiftLeft


    /* Returns true if the value of this is the same as the value of other */
    public boolean equals (LargeInteger other) {
        if ( digits.length != other.digits.length ) return false;
        for (int i = 0 ; i < digits.length ;i++ ) {
            if ( digits[i] != other.digits[i] ) return false;
        }
        return true;
    } // end of equals


    /* Returns true if the value of this is less than the value of other ****/
    public boolean isSmaller (LargeInteger other) {
        if ( digits.length > other.digits.length ) return false;
        if ( digits.length < other.digits.length ) return true;
        for (int i = 0 ; i < digits.length ; i++ ) {
            if ( digits[i] < other.digits[i] ) return true;
            if ( digits[i] > other.digits[i] ) return false;
        }
        return false;
    } // end of isSmaller

    /* This method adds two LargeIntegers: the one on which the method is 
       called and the one given as argument. The sum is returned. The algorithms 
       implemented is the normal digit-by-digit addition with carry. */

    LargeInteger add(LargeInteger other) {
        int size = Math.max( digits.length,other.digits.length );

        /* The sum can have at most one more digit than the two operands */
        LargeInteger sum = new LargeInteger( size + 1 );
        byte carry = 0;

        for (int i = 0; i < size + 1 ;i++) {
            // sumColumn will contain the sum of the two digits at position i plus the carry
            byte sumColumn = carry;
            if ( digits.length - i  - 1 >= 0) sumColumn += digits[ digits.length - i - 1 ];
            if (other.digits.length - i - 1  >= 0) sumColumn += other.digits[ other.digits.length - i - 1 ];
            sum.digits[ sum.digits.length - 1 - i ] = (byte)( sumColumn % 10 ); // The i-th digit in the sum is sumColumn mod 10
            carry = (byte)( sumColumn / 10 );          // The carry for the next iteration is sumColumn/10
        }
        return sum.removeLeadingZeros();
    } // end of add



    /* This method subtracts the LargeInteger other from that from where the method is called.
       Assumption: the argument other contains a number that is not larger than the current number. 
       The algorithm is quite interesting as it makes use of the addition code.
       Suppose numbers X and Y have six digits each. Then X - Y = X + (999999 - Y) - 1000000 + 1.
       It turns out that computing 999999 - Y is easy as each digit d is simply changed to 9-d. 
       Moreover, subtracting 1000000 is easy too, because we just have to ignore the '1' at the 
       first position of X + (999999 - Y). Finally, adding one can be done with the add code we already have.
       This tricks is the equivalent of the method used by most computers to do subtractions on binary numbers. ***/

    public LargeInteger subtract( LargeInteger other ) {
        // if other is larger than this number, simply return 0;
        if (this.isSmaller( other ) || this.equals( other ) ) return new LargeInteger( "0" );

        LargeInteger complement = new LargeInteger( digits.length ); /* complement will be 99999999 - other.digits */
        for (int i = 0; i < digits.length; i++) complement.digits[ i ] = 9;
        for (int i = 0; i < other.digits.length; i++)
            complement.digits[ digits.length - i - 1 ] -= other.digits[other.digits.length - i -  1];

        LargeInteger temp = this.add( complement );     // add (999999- other.digits) to this
        temp = temp.add(new LargeInteger( "1" ));       // add one

        // return the value of temp, but skipping the first digit (i.e. subtracting 1000000)
        // also making sure to remove leading zeros that might have appeared.
        return new LargeInteger(temp.digits,1,temp.digits.length).removeLeadingZeros();
    } // end of subtract


    /* Returns a randomly generated LargeInteger of n digits */
    public static LargeInteger getRandom( int n ) {
        LargeInteger ret = new LargeInteger( n );
        for (int i = 0 ; i < n ; i++) {
            // Math.random() return a random number x such that 0<= x <1
            ret.digits[ i ]=(byte)( Math.floor( Math.random() * 10) );
            // if we generated a zero for first digit, regenerate a draw
            if ( i==0 && ret.digits[ i ] == 0 ) i--;
        }
        return ret;
    } // end of getRandom



    /* Returns a string describing a LargeInteger 17*/
    public String toString () {        

        /* We first write the digits to an array of characters ****/
        char[] out = new char[digits.length];
        for (int i = 0 ; i < digits.length; i++) out[ i ]= (char) ('0' + digits[i]);

        /* We then call a String constructor that takes an array of characters to create the string */
        return new String(out);
    } // end of toString




    /* This function returns the product of this and other by iterative addition */
    public LargeInteger iterativeAddition(LargeInteger other) {
        LargeInteger counter=new LargeInteger("0");
        // initiate increment as LargeInteger
        LargeInteger increment=new LargeInteger("1");
        LargeInteger result=new LargeInteger("0");

        // try the following loops
        try{
            while(!counter.equals(other)){
                result=result.add(this);
                counter=counter.add(increment);
            }
        }catch(Exception e){
            System.out.print("An exception has occured"+e);
        }
        return result;
    } // end of iterativeAddition



    /* This function returns the product of this and other by using the standard multiplication algorithm */
    public LargeInteger standardMultiplication(LargeInteger other) {
        // int lengthofB represents the digits of B
        // the digits are assumed to be within 2^32
        LargeInteger newInt;
        // swap if b is larger than a
        // by doing so, the function can save time
        if(this.isSmaller(other)){
            newInt=other;
            other=this;
        }else{
            newInt=this;
        }
        int counter=0;
        int lengthofA=newInt.digits.length;
        int lengthofB=other.digits.length;
        int maxDigits=lengthofA+lengthofA-1;
        int posShift=0;

        LargeInteger sum=new LargeInteger(maxDigits);


        // try to catch arrayoutofbound exception
        try {
            // looping through all digits of B
            while (counter <lengthofB) {
                LargeInteger result=new LargeInteger(maxDigits);

                int digitofB = other.digits[lengthofB - counter - 1];
                int mod=0;
                // looping through all digits of A
                for (int i = 0; i < lengthofA; i++) {
                    int digitofA = newInt.digits[lengthofA - i - 1];
                    // use direct multiplication for single digit numbers
                    int temp = digitofA * digitofB;
                    if (temp < 10) {
                        // cast the result to byte ( the result will always be under 127)
                        result.digits[maxDigits - i - 1 - posShift] = (byte) (result.digits[maxDigits - i - 1 - posShift] + temp);
                    } else {
                        // check if the number needs to be carried
                        // if so the number will be carried repeatedly to higher digits until nothing left to carry
                        while(temp>9){
                            mod=temp % 10;
                            result.digits[maxDigits - i - 1 - posShift]=(byte)(mod+result.digits[maxDigits - i - 1 - posShift]);
                            temp=(temp-mod)/10;

                        }
                        result.digits[maxDigits - i - 2 - posShift]=(byte)(temp+result.digits[maxDigits-i-2-posShift]);
                    }
                }
                // add everything to the sum, shiftposition to move on to another row
                sum=sum.add(result);
                posShift++;
                counter++;
            }

        } catch(ArrayIndexOutOfBoundsException ae){
            //System.out.println("An error has occured:  "+ae);
        }
        return sum;
    } // end of standardMultiplication


    /* This function returns the product of this and other by using the basic recursive approach described 
       in the homework. Only use the built-in "*" operator to multiply single-digit numbers */
    public LargeInteger recursiveMultiplication( LargeInteger other ) {

        // left and right halves of this and number2                                                                                        
        LargeInteger leftThis, rightThis, leftOther, rightOther;
        LargeInteger term1,  term2,  term3,  term4, sum; // temporary terms                                                                      

        if ( digits.length==1 && other.digits.length==1 ) {
            int product = digits[0] * other.digits[0];
            return new LargeInteger( String.valueOf( product ) );
        }

        int k = digits.length;
        int n = other.digits.length;
        leftThis = new LargeInteger( digits, 0, k - k/2 );
        rightThis = new LargeInteger( digits, k - k/2, k );
        leftOther = new LargeInteger( other.digits, 0, n - n/2 );
        rightOther = new LargeInteger( other.digits, n - n/2, n );

        /* now recursively call recursiveMultiplication to compute the                    
           four products with smaller operands  */

        if ( n > 1 && k > 1 )  term1 = rightThis.recursiveMultiplication(rightOther );
        else term1 = new LargeInteger( "0" );

        if ( k>1 ) term2 = ( rightThis.recursiveMultiplication( leftOther ) ).shiftLeft( n/2 );
        else term2 = new LargeInteger( "0" );

        if ( n>1 ) term3 = ( leftThis.recursiveMultiplication( rightOther ) ).shiftLeft( k/2 );
        else term3 = new LargeInteger( "0" );

        term4 = ( leftThis.recursiveMultiplication( leftOther ) ).shiftLeft( k/2 + n/2 );

        sum = new LargeInteger( "0" );
        sum = sum.add( term1 );
        sum = sum.add( term2 );
        sum = sum.add( term3 );
        sum = sum.add( term4 );

        return sum;
    } // end of recursiveMultiplication             


    /* This method returns the product of this and other by using the faster recursive approach 
       described in the homework. It only uses the built-in "*" operator to multiply single-digit numbers */
    public LargeInteger recursiveFastMultiplication(LargeInteger other) {
        LargeInteger leftthis,rightthis,leftother,rightother;
        LargeInteger term1,term2,term3,sum;
        if (digits.length==1&&other.digits.length==1){
            return new LargeInteger(String.valueOf(digits[0]*other.digits[0]));
        }
        // set the breaking point of each larger integer at k and n
        int k=digits.length;
        int n=other.digits.length;
        // break ech of the LargeInteger in half
        leftthis = new LargeInteger( digits, 0, k - k/2 ); //la
        rightthis = new LargeInteger( digits, k - k/2, k ); //ra

        leftother = new LargeInteger( other.digits, 0, n - n/2 ); //lb
        rightother = new LargeInteger( other.digits, n - n/2, n ); //rb

        if (n<k) return other.recursiveFastMultiplication(this);
        if (k==1) return  this.standardMultiplication(other);
         /* now recursively call recursiveFastMultiplication and add to compute the
           four products with smaller operands  */
        term1=rightthis.recursiveFastMultiplication(rightother);
        term2=leftthis.recursiveFastMultiplication(leftother);
        term3=(leftthis.add(rightthis)).recursiveFastMultiplication((leftother.shiftLeft(n/2-k/2)).add(rightother)).
                subtract((term2.shiftLeft(n/2-k/2))).subtract(term1);

        sum=(term2.shiftLeft(k/2+n/2)).add(term3.shiftLeft(k/2)).add(term1);
        return sum;
    }




}  // end of the LargeInteger class


public class TestLargeInteger {
    public static void main(String args[]) {

        // generate two random numbers of 100 digits each
        LargeInteger a = LargeInteger.getRandom(4);
        LargeInteger b = LargeInteger.getRandom(6);

        System.out.println(a + " + " + b + " = " + a.add( b ) );
        System.out.println(b + " - " + a + " = " + b.subtract( a ) );
        System.out.println(b + " * " + a + " = " + b.recursiveMultiplication( a ) );
        System.out.println(b + " * " + a + " = " + b.recursiveMultiplication( a ) );
        System.out.println("Iterative: "+b + " * " + a + " = " + a.iterativeAddition( b ) );
        System.out.println("Standard:     "+b + " * " + a + " = " + a.standardMultiplication( b ) );
        System.out.println("Recursive:    "+b + " * " + a + " = " + a.recursiveMultiplication( b ) );
        System.out.println("Recursive:    "+b + " * " + a + " = " + a.recursiveFastMultiplication( b ) );
        /*
        LargeInteger a = LargeInteger.getRandom(1000);
        LargeInteger b = LargeInteger.getRandom(1000);
        LargerIntegerPerformance lp=new LargerIntegerPerformance();
        lp.performanceTiming(32);
        */
    }
}

// a class to test the performance of each methods
class LargerIntegerPerformance{
    public void performanceTiming(int rep){
        long timeStart=System.nanoTime();
        long timeAverage;
        long timeElapsed;
        // 60 second in nanosecond
        final long timeLimit=600000000000L;
        // set breakout point
        outerloop:
        for(int j=2;j<=4096;j=j*2) {
            for (int i = 0; i < rep; i++) {
                (LargeInteger.getRandom(j)).iterativeAddition(LargeInteger.getRandom(j));
                timeElapsed=System.nanoTime()-timeStart;
                // a mechamism to stop test when 60 seconds have elapsed and no results have been produced
                if(timeElapsed>timeLimit){
                    System.out.println("60 seconds have elapsed, program terminated");
                    // break out of the nested loop
                    break outerloop;
                }
            }
            // change repitions afer each circle to ex[pediate the test
            rep=rep/2;
            timeAverage=(System.nanoTime()-timeStart)/rep;
            System.out.println("The average of "+rep+" repititions of "+j+" digits is "+timeAverage);
        }
    }
}


































