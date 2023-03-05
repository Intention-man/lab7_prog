package auxiliary_classes;

/**
 *    Fields for form (set of ask user if he wants to add or update a movie)
 *    @param  {int} key
 *    @param  {String} expectedType
 *    @param  {boolean} isNecessary
 *    @param  {String} label
 * */

public class FormField {
    private final int key;
    private final String expectedType;
    private final boolean isNecessary;
    private final String label;

    public FormField(int key, String expectedType, boolean isNecessary, String label) {
        this.key = key;
        this.expectedType = expectedType;
        this.isNecessary = isNecessary;
        this.label = label;
    }

    public int getKey() {
        return key;
    }

    public String getExpectedType() {
        return expectedType;
    }

    public boolean getIsNecessary() {return isNecessary;}
    public String getLabel() {
        return label;
    }
}
