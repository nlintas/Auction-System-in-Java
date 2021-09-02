//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package CommonClasses;

import java.io.Serializable;
import java.util.Objects;

public class Item implements Serializable {

    //Attributes
    private float startingPrice;
    private String name;
    private String description;

    //Constructors
    public Item(float startingPrice, String name, String description) {
        this.startingPrice = startingPrice;
        this.name = name;
        this.description = description;
    }

    //Setters and Getters
    public float getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(float startingPrice) {
        this.startingPrice = startingPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //Methods
    @Override
    public String toString() {
        return "Item{" +
                "startingPrice=" + startingPrice +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Float.compare(item.startingPrice, startingPrice) == 0 &&
                Objects.equals(name, item.name) &&
                Objects.equals(description, item.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startingPrice, name, description);
    }
}
