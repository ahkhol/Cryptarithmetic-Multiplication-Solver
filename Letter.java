public class Letter {
    private char character;
    private int digit;
    private boolean isLeading;
    private boolean domain[];
    private int priority;

    public Letter(char character, boolean isLeading) {
        this.character = character;
        this.isLeading = isLeading;
        priority = 0;
        domain = new boolean[10];
        for (int i = 0; i < domain.length; i++) {
            domain[i] = true;
        }
        if (this.isLeading)
            domain[0] = false;
        digit = -1;
    }

    public void setDomainAt(int index, boolean value) {
        domain[index] = value;
    }

    public boolean domainAt(int index) {
        return domain[index];
    }

    public char getCharacter() {
        return character;
    }

    public void incrementPriority() {
        this.priority++;
    }

    public int getPriority() {
        return priority;
    }

    public boolean setDigit(int digit) {
        if (digit > 9 || digit < 0)
            return false;
        this.digit = digit;
        return true;
    }

    public int getDigit() {
        return digit;
    }

    public void resetDigit() {
        this.digit = -1;
    }

    public boolean isLeading() {
        return isLeading;
    }
}
