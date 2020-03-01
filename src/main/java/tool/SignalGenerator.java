package main.java.tool;

public class SignalGenerator {

    public String source;

    public SignalGenerator(){
        this.source = "s;viave0hlf0wVf'vz0j4sUfna;lsidbf[AKUDB20ej=4f91[ch";
    }

    public SignalGenerator(String newSource) {
        this.source = newSource;
    }

    public String generate(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index = (int) (this.source.length() * Math.random());
            // add Character one by one in end of sb
            sb.append(this.source.charAt(index));
        }
        return sb.toString();
    }
}
