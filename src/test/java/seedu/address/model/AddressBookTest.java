package seedu.address.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ATTENDANCE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_DIET_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_DIET_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_UNUSED;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;
import static seedu.address.testutil.TypicalPersons.DANNY;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.event.Event;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

public class AddressBookTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final AddressBook addressBook = new AddressBook();
    private final AddressBook addressBookWithBobAndAmy = new AddressBookBuilder().withPerson(BOB)
            .withPerson(AMY).build();
    private final AddressBook addressBookWithDanny = new AddressBookBuilder().withPerson(DANNY).build();

    @Test
    public void constructor() {
        assertEquals(Collections.emptyList(), addressBook.getPersonList());
        assertEquals(new Event(), addressBook.getEventDetails());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        addressBook.resetData(null);
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        // Two persons with the same identity fields
        Person editedAlice = new PersonBuilder(ALICE).withAttendance(VALID_ATTENDANCE_BOB).withTags(VALID_TAG_DIET_BOB)
                .build();
        List<Person> newPersons = Arrays.asList(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newPersons);

        thrown.expect(DuplicatePersonException.class);
        addressBook.resetData(newData);
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        addressBook.hasPerson(null);
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        assertTrue(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withAttendance(VALID_ATTENDANCE_BOB).withTags(VALID_TAG_DIET_BOB)
                .build();
        assertTrue(addressBook.hasPerson(editedAlice));
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        thrown.expect(UnsupportedOperationException.class);
        addressBook.getPersonList().remove(0);
    }

    //@@author aaryamNUS
    @Test
    public void removeTag_nonExistentTag_addressBookUnchanged() {
        addressBookWithBobAndAmy.removeTag(new Tag(VALID_TAG_UNUSED));

        AddressBook expectedAddressBook = new AddressBookBuilder().withPerson(BOB).withPerson(AMY).build();

        assertEquals(expectedAddressBook, addressBookWithBobAndAmy);
    }

    @Test
    public void removeTag_sharedTagOfDifferentPersons_tagRemoved() {
        addressBookWithBobAndAmy.removeTag(new Tag(VALID_TAG_FRIEND));
        addressBookWithBobAndAmy.removeTag(new Tag(VALID_TAG_FRIEND));

        Person amyWithoutFriendTag = new PersonBuilder(AMY).withTags(VALID_TAG_DIET_AMY).build();
        Person bobWithoutFriendTag = new PersonBuilder(BOB).withTags(VALID_TAG_DIET_BOB).build();
        AddressBook expectedAddressBook = new AddressBookBuilder().withPerson(bobWithoutFriendTag)
                .withPerson(amyWithoutFriendTag).build();

        assertEquals(expectedAddressBook, addressBookWithBobAndAmy);
    }

    @Test
    public void addTag_noNewTagsToAdd_addressBookUnchanged() {
        addressBookWithDanny.addTag(new Tag(VALID_TAG_FRIEND));

        AddressBook expectedAddressBook = new AddressBookBuilder().withPerson(DANNY).build();

        assertEquals(expectedAddressBook, addressBookWithDanny);
    }

    @Test
    public void addTag_tagAddedToMultiplePersons_tagAdded() {
        addressBookWithBobAndAmy.addTag(new Tag(VALID_TAG_HUSBAND));

        Person amyWithHusbandTag = new PersonBuilder(AMY).withTags(VALID_TAG_DIET_AMY, VALID_TAG_HUSBAND).build();
        Person bobWithHusbandTag = new PersonBuilder(BOB).withTags(VALID_TAG_DIET_BOB, VALID_TAG_HUSBAND).build();
        AddressBook expectedAddressBook = new AddressBookBuilder().withPerson(bobWithHusbandTag)
                .withPerson(amyWithHusbandTag).build();

        assertEquals(expectedAddressBook, addressBookWithBobAndAmy);
    }
    //@@author aaryamNUS

    /**
     * A stub ReadOnlyAddressBook whose persons list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();
        private final Event eventDetails = new Event();
        AddressBookStub(Collection<Person> persons, Event event) {
            this.persons.setAll(persons);
            this.eventDetails.setEvent(event);
        }

        AddressBookStub(Collection<Person> persons) {
            this.persons.setAll(persons);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }

        @Override
        public Event getEventDetails() {
            return eventDetails;
        }
    }
}
