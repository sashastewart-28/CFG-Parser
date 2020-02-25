import java.util.Scanner;
import java.io.*;
class Main 
{
  static String  grammer[][]; //grammer array,stores production rules
  static int np = 0; //no of production rules for both default grammer
  static String ans_mat[][] = new String[10][10]; //CYK array
  static Scanner in = new Scanner(System.in); //for input if required
  static String word; //word to be checked
  static String start; //start symbol
  static Scanner sc,s; //sc for reading file and, s for reading and counting no of lines in the text file
  static int c; //no of production rules for grammar from file
  static int len[]; //length of each production rule
  //Checks if file or word or both are given as input
  public static void checkInput(String args[])
  {
    String defaultgrammer[][]={{"S", "AB", "BC"}, {"A", "BA", "a"}, {"B", "CC", "b"}, {"C", "AB", "a"}};
    np=defaultgrammer.length;
    int l=args.length;
    if(l==0)   //In this case we use the default grammer
    {
      System.out.println("The default grammer is:-");
      for(int i=0;i<np-1;i++)
      {
        for(int j=0;j<np-1;j++)
        {
          System.out.print(defaultgrammer[i][j]+" ");
        }
        System.out.println();
      }
      System.out.println("if want to enter file"); 
      System.out.println("--> Usage: java Main <filename>"); 
      System.out.println("-->Usage: java Main <filename> <word>");
      System.out.println("Enter the string to be checked >>");
      word = in.nextLine();
      grammer=defaultgrammer;
    }
    if(l==1)  //case 2 text file given as command line argument
    {
      readFile(args);
      np=grammer.length;
      System.out.println("The grammer is:-");
      display();
      System.out.println("Enter the string to be checked >>");
      word = in.nextLine();
    }
    if(l==2)  //case 3 both text file and word given as command line argument
    {
      readFile(args);
      np=grammer.length;
      System.out.println("The grammer is:-");
      display();
      word=args[1];
    }

  }//end of checkInput()
  //reads the given text file and puts it into the grammer array,counts no of production rules and calculates length of each production rule
  public static void readFile(String args[])
  {
      c=0;//counter for no of production rules
      try
      {
          s=new Scanner(new File(args[0]));
      }
      catch(Exception e){System.out.println("File error");}
      while(s.hasNextLine())
      {
        c++;
        s.nextLine();
      }
      len=new int[c];
      System.out.println("The no of production rules are "+c);
      s.close();
      grammer=new String[c][c];
      try
      {
        sc=new Scanner(new File(args[0]));
      }
      catch(Exception e){System.out.println("File error");}
      int i=0;
      while(sc.hasNextLine())
      {
        String line=sc.nextLine();
        String part[]=line.split(" ");
        for(int j=0;j<part.length;j++)
        {
          grammer[i][j]=part[j];
        }
        len[i]=part.length;
        i++;
      }
  }//end of readFile()
  //checks if any prduction rule exists for the passed string
  public static String check(String a)
  {
      String to_ret = "";
      int count = 0;
      for(int i = 0; i < np; i++)
      {
        for(count = 0; count < grammer[i].length;count++)
        { 
          if(grammer[i][count]!=null)
          {
            if(grammer[i][count].equals(a))
            {
              to_ret += grammer[i][0];
            }//end if(.equals())
          }//end if(!=null)
        }//end inner for
      }//end outer for
      return to_ret;
  } //end check()
  //Makes all possible combinations out of the two string passed
  public static String combi(String a, String b)
  {
    String to_ret = "", temp = "";
    for(int i = 0; i < a.length(); i++)
    {
      for(int j = 0; j < b.length(); j++)
      {
        temp = "";
        temp += a.charAt(i) + "" +  b.charAt(j);
        to_ret += check(temp);
      }//end inner loop
    }//end outer loop
    return to_ret;
  }//end combi()
  //creates the CYK matrix
  public static void createMat()
  {
    int count;
    String st = "", r = "";
    //Fill the diagnol of the matrix (first iteration of algorithm)
    //base case i.e. for only one input symbol
    for(int i = 0; i < word.length(); i++)
    {
      r = ""; //production rule will be stored here
      st = "" + word.charAt(i);
      for(int j = 0; j < grammer.length; j++)
      {
        for(count = 1; count < grammer[j].length; count++)
        {
          if(grammer[j][count]!=null)
          {
            if(grammer[j][count].equals(st))
            {
              r += grammer[j][0];
            }//end inner if
          }//end outer if
        }//end inner for      
      }//end outer for
      ans_mat[i][i] = r; //production rule ultimately stored in CYK matrix
    }//end base case
    //Fill the rest of the matrix
    //The computed values from above base case are used to compute these values
    for(int i = 1; i < word.length(); i++)
    {
      for(int j = i; j < word.length(); j++)
      {
        r = ""; 
        for(int k = j - i; k < j; k++)
        {
          r += combi(ans_mat[j - i][k], ans_mat[k + 1][j]);
        }
        if(r.length()==0)
          ans_mat[j-i][j]="#"; //if no production rule produces the specified string 
        else
          ans_mat[j - i][j] = r;
      }//end outer for(j)
    }//end for(i)
  }//end createMat()
  //accept state
  public static void accept() 
  {
    System.out.println("String is accepted");
  }
  //reject state
  public static void reject() 
  {
    System.out.println("String is rejected");
  }
  //for displaying the grammar
  public static void display() 
  {
    for(int k=0;k<c;k++)
    {
      for(int j=0;j<len[k];j++)
      {
        System.out.print(grammer[k][j]+" ");
      }
      System.out.println();
    }
  }
  //for displaying the CYK matrix
  public static void displayCYK() 
  {
    for(int i=0;i<word.length();i++)
    {
      for(int j=0;j<word.length();j++)
      {
        System.out.print(ans_mat[i][j]+" ");
      }
      System.out.println();
    }
  }
  public static void main(String[] args) 
  {
    start = "S";
    checkInput(args); //decides whether to read file or use default grammar
    for(int i=0;i<10;i++) //Initializing the cyk matrix
    {
      for(int j=0;j<10;j++)
      {
        ans_mat[i][j]="#";
      }
    }
    createMat();
    System.out.println("The CYK matrix is: ");
    displayCYK();
    //The last column of first row should have the start symbol
    if(ans_mat[0][word.length() - 1].indexOf(start) >= 0)
    {
      accept();
    }
    else
    {
      reject();
    }
    in.close();   
  }  //end main method
}//ens class
//end program