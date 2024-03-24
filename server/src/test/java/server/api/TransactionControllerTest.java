package server.api;

import commons.Event;
import commons.Person;
import commons.Transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;


class TransactionControllerTest {

    private TestTransactionRepository db;
    private TransactionController sut;
    @BeforeEach
    public void setup () {
        db = new TestTransactionRepository();
        sut = new TransactionController(db);
    }
    @Test
    public void getTransactionNullTest() {
        var actual = sut.getById(-14);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }
    @Test
    public void deleteTransactionNullTest() {
        var actual = sut.deleteById(-1);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotAddNullTransaction() {
        Transaction test = null;
        var actual = sut.add(test);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }
    @Test
    public void testGetByInvalidId() {
        int invalidID = -1;
        assertEquals(sut.getById(invalidID), ResponseEntity.badRequest().build());
    }
    @Test
    public void testAddValidTransaction() {
        HashSet<Person> participants = new HashSet<>();

        Person person1 = new Person("test@email.com", "First", "Test",
                "iban33", new Event("", "", 1, "", new HashSet<>(),
                new HashSet<>()), new HashSet<>(), new HashSet<>());

        Person person2 = new Person("test@email.com", "First", "Test",
                "iban33", new Event("", "", 2, "", new HashSet<>(),
                new HashSet<>()), new HashSet<>(), new HashSet<>());

        participants.add(person1);
        participants.add(person2);

        Transaction t = new Transaction("test",
                LocalDate.of(Integer.parseInt("1970"), Integer.parseInt("10"), Integer.parseInt("10")),
                100, 947,participants, person1, "Euro");

        // Act
        ResponseEntity<Transaction> actualResponse = sut.add(t);

        // Assert
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(t, actualResponse.getBody());
    }

    /**
     * Tests adding transaction without participants.
     */
    @Test
    public void testAddInvalidTransaction() {
        HashSet<Person> participants = new HashSet<>();

        Person person1 = new Person("test@email.com", "First", "Test",
                "iban33", new Event("", "", 1, "", new HashSet<>(),
                new HashSet<>()), new HashSet<>(), new HashSet<>());

        Transaction t = new Transaction("test",
                LocalDate.of(Integer.parseInt("1970"), Integer.parseInt("10"), Integer.parseInt("10")),
                100, 947,participants, person1, "Euro");

        // Act
        ResponseEntity<Transaction> actualResponse = sut.add(t);

        // Assert
        assertEquals(BAD_REQUEST, actualResponse.getStatusCode());
    }

    /**
     * Tests adding null instead of Transaction.
     */
    @Test
    public void testAddNull() {
        // Act
        ResponseEntity<Transaction> actualResponse = sut.add(null);

        // Assert
        assertEquals(BAD_REQUEST, actualResponse.getStatusCode());
    }

    /**
     * Tests deleting Transaction by its id.
     */
    @Test
    public void testDeleteValidTransaction() {
        // Create a sample transaction
        HashSet<Person> participants = new HashSet<>();

        Person person1 = new Person("test@email.com", "First", "Test",
                "iban33", new Event("", "", 1, "", new HashSet<>(),
                new HashSet<>()), new HashSet<>(), new HashSet<>());

        Person person2 = new Person("test@email.com", "First", "Test",
                "iban33", new Event("", "", 2, "", new HashSet<>(),
                new HashSet<>()), new HashSet<>(), new HashSet<>());

        participants.add(person1);
        participants.add(person2);

        Transaction t = new Transaction("test",
                LocalDate.of(Integer.parseInt("1970"), Integer.parseInt("10"), Integer.parseInt("10")),
                100, 947,participants, person1, "Euro");

        // Save the transaction to the repository
        sut.add(t);

        // Act: Delete the transaction by ID
        ResponseEntity<Transaction> actualResponse = sut.deleteById(t.getId());

        // Assert
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(t, actualResponse.getBody());
    }

    /**
     * Tests updating the name of already existing Transaction.
     */
    @Test
    public void testUpdateNameValidTransaction() {
        // Create a sample transaction
        HashSet<Person> participants = new HashSet<>();

        Person person1 = new Person("test@email.com", "First", "Test",
                "iban33", new Event("", "", 1, "", new HashSet<>(),
                new HashSet<>()), new HashSet<>(), new HashSet<>());

        Person person2 = new Person("test@email.com", "First", "Test",
                "iban33", new Event("", "", 2, "", new HashSet<>(),
                new HashSet<>()), new HashSet<>(), new HashSet<>());

        participants.add(person1);
        participants.add(person2);

        Transaction t = new Transaction("test",
                LocalDate.of(Integer.parseInt("1970"), Integer.parseInt("10"), Integer.parseInt("10")),
                100, 947,participants, person1, "Euro");

        // Save the transaction to the database
        sut.add(t);

        // New name for the transaction
        String newName = "New Transaction Name";

        // Act: Update the name of the transaction by ID
        ResponseEntity<Transaction> actualResponse = sut.updateNameById(t.getId(), newName);

        // Assert
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(newName, actualResponse.getBody().getName());
    }

    /**
     * Tests updating all the values of the transaction at once.
     */
    @Test
    public void testUpdateAllValuesOfTransactionById() {
        // Create a sample transaction
        HashSet<Person> participants = new HashSet<>();

        Person person1 = new Person("test@email.com", "First", "Test",
                "iban33", new Event("", "", 1, "", new HashSet<>(),
                new HashSet<>()), new HashSet<>(), new HashSet<>());

        Person person2 = new Person("test@email.com", "First", "Test",
                "iban33", new Event("", "", 2, "", new HashSet<>(),
                new HashSet<>()), new HashSet<>(), new HashSet<>());

        participants.add(person1);
        participants.add(person2);

        Transaction t = new Transaction("test",
                LocalDate.of(Integer.parseInt("1970"), Integer.parseInt("10"), Integer.parseInt("10")),
                100, 947,participants, person1, "Euro");

        // Save the transaction to the database
        sut.add(t);

        // New name for the transaction
        String newName = "New Transaction Name";
        LocalDate newDate = LocalDate.of(2004,10,27);
        int newMoney = 70;
        int newCurrency = 1001;
        HashSet<Person> newParticipants = new HashSet<>();
        newParticipants.add(person2);
        String newExpenseType = "Dollars";
        Transaction newTransaction = new Transaction(newName, newDate,newMoney, newCurrency, newParticipants, person2, newExpenseType);

        // Act: Update the name of the transaction by ID
        ResponseEntity<Transaction> actualResponse = sut.updateById(1, newTransaction);

        // Assert
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(newTransaction, actualResponse.getBody());
    }


    @Test
    void getAll() {
    }

    @Test
    void getById() {
    }

    @Test
    void add() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void updateNameById() {
    }

    @Test
    void updateParticipantsById() {
    }

    @Test
    void updateCreatorById() {
    }
}