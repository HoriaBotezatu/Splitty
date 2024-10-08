package commons;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Transaction class.
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected int id;

    protected String name;
    protected LocalDate date;

    protected double money;

    protected int currency;
    protected String expenseType;

    protected boolean handOff;


    @ManyToMany(fetch = FetchType.EAGER)
    public List<Person> participants;

    @ManyToOne
    public Person creator;

    /**
     * Private empty constructor for the Transaction class.
     */
    @SuppressWarnings("unused")
    protected Transaction() {
        // for object mapper
    }

    /**
     * Public class for creating a transaction.
     * @param name - name of the transaction
     * @param date - date of the transaction
     * @param money - value of the transaction
     * @param currency - the currency in which the transaction is handled
     */
    public Transaction(String name, LocalDate date, double money, int currency,
                       List<Person> participants, Person creator, String expenseType) {
        id++;
        this.name = name;
        this.date = date;
        this.money = money;
        this.currency = currency;
        this.participants = participants;
        this.creator = creator;
        this.expenseType = Objects.requireNonNullElse(expenseType, "Other");
        this.handOff = false;
    }

    /**
     * Public class for creating a transaction without specified id.
     * @param name - name of the transaction
     * @param date - date of the transaction
     * @param money - value of the transaction
     * @param currency - the currency in which the transaction is handled
     * @param expenseType - type of expense
     * @param participants - participants
     * @param creator - creator
     */
    public Transaction(String name, LocalDate date, double money, int currency, String expenseType, List<Person> participants, Person creator) {
        this.name = name;
        this.date = date;
        this.money = money;
        this.currency = currency;
        this.expenseType = expenseType;
        this.participants = participants;
        this.creator = creator;
        this.handOff = false;
        // calculateDebts();
    }


    /**
     * Getter for handOff.
     * @return true if this is a handoff
     */
    public boolean isHandOff() {
        return handOff;
    }

    /**
     * Setter for handoff.
     * @param handOff determining if this is a handoff or not
     */
    public void setHandOff(boolean handOff) {
        this.handOff = handOff;
    }

    /**
     * Getter for the id of a transaction.
     * @return - int representation of an id.
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the name of a transaction.
     * @return - String representation of the name of the transaction.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the Date of the transaction.
     * @return - Date representation of the transaction.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Getter for the value of the transaction.
     * @return - double representation of the amount to be transferred.
     */
    public double getMoney() {
        return money;
    }

    /**
     * Getter for the currency in which the transaction is being done.
     * @return - Currency representation of the amount to be transferred.
     */
    public int getCurrency() {
        return currency;
    }

    /**
     * Getter for the expense type of the transaction.
     * @return - Expense type of transaction.
     */
    public String getExpenseType() {
        return expenseType;
    }

    /**
     * Getter for the people involved in a transaction.
     * @return - a set of all the people involved in the transaction.
     */
    public List<Person> getParticipants() {
        return participants;
    }

    /**
     * Getter fot the creator of the transaction.
     * @return - the creator of the transaction.
     */
    public Person getCreator() {
        return creator;
    }

    /**
     * Setter for the name of the transaction.
     * @param name - name of the transaction.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter for the date of the transaction.
     * @param date - date of the transaction.
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Setter for the value of the transaction.
     * @param money - value of the transaction.
     */
    public void setMoney(double money) {
        this.money = money;
    }

    /**
     * Setter for the currency of the transaction.
     * @param currency - currency in which the transaction is being made.
     */
    public void setCurrency(int currency) {
        this.currency = currency;
    }

    /**
     * Setter for the set of participants included in the transaction.
     * @param participants set of participant.
     */
    public void setParticipants(List<Person> participants){
        this.participants = participants;
    }

    /**
     * setter for the creator of the transaction.
     * @param creator - the creator of the transaction.
     */
    public void setCreator(Person creator){
        this.creator = creator;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }



    /**
     * toString method for the Transaction class.
     * @return - string representation of a Transaction with all the data
     */
    @Override
    public String toString() {
        return name + ", date: " + date +
                ", " + money + " EUR";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Transaction that = (Transaction) o;
        return id == that.id ;
        /** && Double.compare(money, that.money) == 0 && currency == that.currency &&
                Objects.equals(name, that.name) && Objects.equals(date, that.date) &&
                Objects.equals(expenseType, that.expenseType) &&
                Objects.equals(participants, that.participants) && Objects.equals(creator, that.creator);
         */
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a list of id of the participants.
     * @return
     */
    @JsonIgnore
    public List<Integer> getParticipantsIds() {
        List<Integer> ret = new LinkedList<>();
        if (participants == null) {return ret;}
        for (Person person: participants) {ret.add(person.getId());}
        return ret;
    }
}
