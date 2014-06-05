/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cellularautomata.core;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * Given a rule in the format specified in TheFormat, read it and prepare a setup
 * for a CA.
 * @author lagravas
 */
public class RuleDecoder {

    public static final String property = "[a-z]+:[a-z]+\\s*(#.*){0,1}";
    public static final String variables = "var\\s+([a-z]+)=\\{((\\d+(,{0,1}))+)\\}\\s*(#.*){0,1}";
    public static final String ruleString = "([a-z0-9,{0,1}]+)\\s*(#.*){0,1}";
    public static final String comment = "^#.*";

    // properties will contain (property,value) pairs
    private HashMap<String,String> properties;
    // vars will contain (variable,values) pairs
    private HashMap<Character,int[]> vars;
    // rules will contain an integer table which represents the rule
    private ArrayList<int[]> rules;

    private Vector<String> lines;

    public RuleDecoder(String fileName){
        lines = new Vector<String>();
        properties = new HashMap<String, String>();
        vars = new HashMap<Character,int[]>();
        rules = new ArrayList<int[]>();

        readFile(fileName);

    }

    public void readFile(String fileName) {
        try {
            FileInputStream fstream = new FileInputStream(fileName);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        catch (IOException e){
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void process() {
        for (String line : lines){
            // process a declaration type of string
            if (line.matches(property)) processProperty(property); 
            // ignore the comments
            if (line.matches(comment)) continue;
            // save the variable values
            if (line.matches(variables)) processVariable(line);
            // store the corresponding rule
            if (line.matches(ruleString)) processRule(line);
        }
    }

    private void processProperty(String declaration){
        List<String> l = Arrays.asList(declaration.split(":"));
        properties.put(l.get(0),l.get(1));
    }

    private void processVariable(String variable){
        Pattern p = Pattern.compile(variables);
        Matcher m = p.matcher(variable);
        // execute the matching
        boolean matchFound = m.find();
        if (matchFound) { // Get all groups for this match
           String varName = m.group(1);
           String[] values = m.group(2).split("\\,");
           int[] integerValues = new int[values.length];
           for (int i = 0; i < values.length; ++i){
               integerValues[i] = Integer.parseInt(values[i]);
           }
           // putting the results in the var HashMap
           vars.put(new Character(varName.charAt(0)), integerValues);
        }
        else{
            System.out.println("Merdeeee");
        }
    }

    private void processRule(String ruleStr){
        Pattern p = Pattern.compile(ruleString);
        Matcher m = p.matcher(ruleStr);
        // execute the matching
        boolean matchFound = m.find();
        // erase the eventual comments and get the numbers
        String effectiveRule = m.group(1);
        if (matchFound){
            // test if the rule has or not ,
            if (effectiveRule.indexOf(",") > 0){
                String[] values = m.group(1).split(",");
                processComaSeparatedRule(values);
            }
            // if it has no , then every digit is an entry
            else {
                // test if there are no variables
                if (containsOnlyNumbers(effectiveRule)){
                    int[] rule = new int[effectiveRule.length()];
                    for (int i = 0; i < effectiveRule.length(); ++i){
                        rule[i] = (int) (effectiveRule.charAt(i) - '0');
                    }
                    rules.add(rule);

                } else {
                    // if there are variables call the specialized function
                    char[] decomposedRule = effectiveRule.toCharArray();
                    processSimpleRuleWithVariables(decomposedRule);
                }
            }
            
        
        }
    }

    /**
     * 
     * @param s
     * @return
     */
    private boolean containsOnlyNumbers(String s){
        for (int i = 0; i < s.length(); ++i){
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }

    /**
     * Processing and inserting in the rules list a "," separated rule.
     * @param rule
     */
    private void processComaSeparatedRule(String[] rule){
        
    }

    /**
     * 
     * @param rule
     */
    private void processSimpleRuleWithVariables(char[] rule){
        int countVariableNumber = 0;
        int rulesNumber = 1;

        for (char c : rule){
            if (Character.isLetter(c)) {
                countVariableNumber++;
                int[] values = vars.get(new Character(c));
                rulesNumber *= values.length;
            }
        }
        // for each variable value
        for (int i = 0; i < rulesNumber; ++i){
            int[] newRule = new int[rule.length];
            for (int var = 0; var < rule.length; ++var){
                
            }

        }
    }

    public static void main(String[] args) throws IOException {
        
        RuleDecoder rd = new RuleDecoder("example.table");
        rd.process();
    }
}
