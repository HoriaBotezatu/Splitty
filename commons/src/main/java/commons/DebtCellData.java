package commons;

public class DebtCellData {

  protected Person sender;
  protected Person receiver;
  protected double debt;

  /**
   * constructor which initializes all attributes.
   * @param sender
   * @param receiver
   * @param debt
   */
  public DebtCellData(Person sender, Person receiver, double debt){
    this.sender = sender;
    this.receiver = receiver;
    this.debt = debt;
 }

  /**
   * Getter for the amount of debt.
   * @return
   */
  public double getDebt() {
    return debt;
  }

  /**
   * Getter for the sender Person.
   * @return
   */
  public Person getSender() {
    return sender;
  }
  /**
   * Getter for the receiver Person.
   * @return
   */
  public Person getReceiver() {
    return receiver;
  }
}