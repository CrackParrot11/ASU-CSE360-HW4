package application;

import databasePart1.DatabaseHelper;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Test cases for the Q&amp;A System CRUD operations
 */
public class QASystemTest {
	/**
	 * Define reference variables to be used in testing
	 */
    private static DatabaseHelper db;
    private static User testUser1;
    private static User testUser2;

	/**
     * This method sets up the database for use in testing.
     */
    @BeforeAll
    static void setupDatabase() throws SQLException {
        db = new DatabaseHelper();
        db.connectToDatabase();
        // Create test users
        testUser1 = new User("testStudent1", "Pass123!", "user");
        testUser1.setEmail("test1@asu.edu");
        testUser2 = new User("testStudent2", "Pass456!", "user");
        testUser2.setEmail("test2@asu.edu");
        if (!db.doesUserExist("testStudent1")) {
            db.register(testUser1);
        }
        if (!db.doesUserExist("testStudent2")) {
            db.register(testUser2);
        }
    }

	/**
     * Closes the connection to the database.
     */
    @AfterAll
    static void cleanup() {
        db.closeConnection();
    }

    // ========== Question CRUD Tests ==========

    /**
     * Tests successful creation of a question with valid title and content.
     * <p>
     * Verifies that:
     * <ul>
     * <li>A positive question ID is returned from the database</li>
     * <li>The ID is correctly set on the {@link Question} object</li>
     * </ul>
     *
     * @throws SQLException if insertion fails unexpectedly
     * @see DatabaseHelper#createQuestion(Question)
     * @since 1.0
     */
    @Test
    @DisplayName("Test 1: Create a valid question")
    void testCreateValidQuestion() throws SQLException {
        Question q = new Question(
            "How do I implement a linked list?",
            "I'm trying to create a linked list in Java but getting confused about pointers.",
            testUser1.getUserName()
        );

        int id = db.createQuestion(q);
        assertTrue(id > 0, "Question ID should be positive");
        assertEquals(id, q.getId(), "Question object should have correct ID");
    }

    /**
     * <p>Tests that a title shorter than 5 characters is rejected.</p>
     * <p>
     * The {@link Question} constructor should throw Illegal Argument Exception
     * when the title length is invalid.
     * </p>
     * @throws IllegalArgumentException If the question constructor is passed an illegal argument.
     */
    @Test
    @DisplayName("Test 2: Create a question with invalid title (too short)")
    void testCreateQuestionInvalidTitleTooShort() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Question("Help", "I need help with my code", testUser1.getUserName());
        }, "Should throw exception for title less than 5 characters");
    }

    /**
     * <p>Tests that a title longer than 200 characters is rejected.</p>
     * <p>
     * Constructs a 201-character title to trigger the length validation.
     * </p>
     * @throws IllegalArgumentException If the question constructor is passed an illegal argument.
     */
    @Test
    @DisplayName("Test 3: Create a question with invalid title (too long)")
    void testCreateQuestionInvalidTitleTooLong() {
        String longTitle = "A".repeat(201);
        assertThrows(IllegalArgumentException.class, () -> {
            new Question(longTitle, "Content here", testUser1.getUserName());
        }, "Should throw exception for title over 200 characters");
    }

    /**
     * <p>Tests that an empty title is not allowed.</p>
     * <p>
     * An empty string should trigger an Illegal Argument Exception
     * during question construction.
     * </p>
     * @throws IllegalArgumentException If the question constructor is passed an illegal argument.
     */
    @Test
    @DisplayName("Test 4: Create a question with empty title")
    void testCreateQuestionEmptyTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Question("", "Some content", testUser1.getUserName());
        }, "Should throw exception for empty title");
    }

    /**
     * <p>Tests that content shorter than 10 characters is rejected.</p>
     * <p>
     * Uses a 5-character content string to validate minimum length requirement.
     * </p>
     * @throws IllegalArgumentException If the question constructor is passed an illegal argument.
     */
    @Test
    @DisplayName("Test 5: Create a question with invalid content (too short)")
    void testCreateQuestionInvalidContentTooShort() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Question("Valid Title Here", "Short", testUser1.getUserName());
        }, "Should throw exception for content less than 10 characters");
    }

	/**
     * <p>Tests that content longer than 2000 characters is rejected.</p>
     * <p>
     * Generates a 2001 character long string and attempts to create a new Question, 
     * should trigger an exception.
     * </p>
     * @throws IllegalArgumentException If the question constructor is passed an illegal argument.
     */
    @Test
	@DisplayName("Test 6: Create question with invalid content (too long)")
	void testCreateQuestionInvalidContentTooLong() {
    String longContent = "A".repeat(2001);
    	assertThrows(IllegalArgumentException.class, () -> {
        	new Question("Valid Title", longContent, testUser1.getUserName());
    	}, 	"Should throw exception for content over 2000 characters");
	}

	/**
     * <p>Tests that empty content is not allowed.</p>
     * <p>
     * An empty string should trigger an Illegal Argument Exception
     * during question construction.
     * </p>
     * @throws IllegalArgumentException If the question constructor is passed an illegal argument.
     */
    @Test
    @DisplayName("Test 7: Create question with empty content")
    void testCreateQuestionEmptyContent() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Question("Valid Title", "", testUser1.getUserName());
        }, "Should throw exception for empty content");
    }

	/**
     * <p>Tests that a question can be read by its id.</p>
     * <p>
     * Creates a question then retrieves it by its id. Verifies that the question was successfully read by 
     * comparing actual and expected values.
     * </p>
     * @throws SQLException If database error occurs.
     */
    @Test
    @DisplayName("Test 8: Read question by ID")
    void testReadQuestionById() throws SQLException {
        Question q = new Question(
            "What is polymorphism?",
            "Can someone explain polymorphism with examples?",
            testUser1.getUserName()
        );
        int id = db.createQuestion(q);
        Question retrieved = db.getQuestionById(id);
        assertNotNull(retrieved, "Should retrieve the question");
        assertEquals(q.getTitle(), retrieved.getTitle(), "Titles should match");
        assertEquals(q.getContent(), retrieved.getContent(), "Content should match");
    }

	/**
     * <p>Tests that attempting to read a question that doesn't exist returns null.</p>
     * <p>
     * Verifies that the return value is null when reading a question that doesn't exist.
     * </p>
     * @throws SQLException If database error occurs.
     */
    @Test
    @DisplayName("Test 9: Read non-existent question")
    void testReadNonExistentQuestion() throws SQLException {
        Question retrieved = db.getQuestionById(99999);
        assertNull(retrieved, "Should return null for non-existent question");
    }

	/**
     * <p>Tests that a question title and content can be updated.</p>
     * <p>
     * This test creates a question then attempts to update its title and its content. The updates 
     * are verified by checking that the actual values match their expected values.
     * </p>
     * @throws SQLException If database error occurs.
     */
    @Test
    @DisplayName("Test 10: Update question title and content")
    void testUpdateQuestion() throws SQLException {
        Question q = new Question(
            "Original Title",
            "Original content here",
            testUser1.getUserName()
        );
        int id = db.createQuestion(q);
        q.setTitle("Updated Title");
        q.setContent("Updated content with more details");
        boolean updated = db.updateQuestion(q);
        assertTrue(updated, "Update should succeed");
        Question retrieved = db.getQuestionById(id);
        assertEquals("Updated Title", retrieved.getTitle(), "Title should be updated");
        assertEquals("Updated content with more details", retrieved.getContent(), "Content should be updated");
    }

	/**
     * <p>Test: Update question with invalid title.</p>
     * <p>
     * Verifies that an IllegalArgumentException is thrown when attempting to update a question with an invalid title.
     * </p>
     * @throws SQLException If database error occurs.
     * @throws IllegalArgumentException If the question constructor is passed an illegal argument.
     */
    @Test
    @DisplayName("Test 11: Update question with invalid title")
    void testUpdateQuestionInvalidTitle() throws SQLException {
        Question q = new Question(
            "Valid Original Title",
            "Valid content here",
            testUser1.getUserName()
        );
        db.createQuestion(q);
        assertThrows(IllegalArgumentException.class, () -> {
            q.setTitle("Bad");
        }, "Should throw exception when updating to invalid title");
    }

	/**
     * <p> Test: Delete question </p>
     * <p> 
     * This test verifies that a question can be deleted. This is verified by checking that the database method returns true 
     * and that the question id no longer exists after deletion.
     * </p>
     * 
     * @throws SQLException If a database error occurs
     */
    @Test
    @DisplayName("Test 12: Delete question")
    void testDeleteQuestion() throws SQLException {
        Question q = new Question(
            "Question to Delete",
            "This question will be deleted",
            testUser1.getUserName()
        );
        int id = db.createQuestion(q);
        boolean deleted = db.deleteQuestion(id, testUser1.getUserName());
        assertTrue(deleted, "Deletion should succeed");
        Question retrieved = db.getQuestionById(id);
        assertNull(retrieved, "Deleted question should not exist");
    }

	/**
     * <p> Test: Delete question by wrong user </p>
     * <p> 
     * This test verifies that a question can only be deleted by the user who posted it. This is verified by checking that the 
     * database method returns false when another user attempts to delete the question. The question id is also checked to make 
     * sure it still exists.
     * </p>
     * 
     * @throws SQLException If a database error occurs
     */
    @Test
    @DisplayName("Test 13: Delete question by wrong user")
    void testDeleteQuestionByWrongUser() throws SQLException {
        Question q = new Question(
            "Question by User1",
            "This question belongs to user1",
            testUser1.getUserName()
        );
        int id = db.createQuestion(q);
        boolean deleted = db.deleteQuestion(id, testUser2.getUserName());
        assertFalse(deleted, "User2 should not be able to delete User1's question");
        Question retrieved = db.getQuestionById(id);
        assertNotNull(retrieved, "Question should still exist");
    }

    // ========== Answer CRUD Tests ==========

    /**
     * <p> Test: Create valid answer </p>
     * <p> 
     * Tests for valid answer creation. A valid answer is one which is above the minimum character limit 
     * and below the maximum, is assigned to the correct question, and is added to the database with its own ID. 
     * Test verifies the answer by checking if the new answer has an ID and that the ID equals its expected value.
     * </p>
     *
     * @throws SQLException If a database error occurs
     * 
     * @see QASystemTest#testCreateValidQuestion()
     */

    @Test
    @DisplayName("Test 14: Create valid answer")
    void testCreateValidAnswer() throws SQLException {
        Question q = new Question(
            "Test Question for Answer",
            "Question content here",
            testUser1.getUserName()
        );
        int qId = db.createQuestion(q);
        Answer a = new Answer(qId, "This is my answer to your question", testUser2.getUserName());
        int aId = db.createAnswer(a);
        assertTrue(aId > 0, "Answer ID should be positive");
        assertEquals(aId, a.getId(), "Answer should have correct ID");
    }

    /**
     * <p> Test: Create answer with too few characters (Invalid) </p>
     * <p> 
     * Tests an invalid answer. In this test an invalid answer is one which doesn't have enough characters
     * (less than 5 characters). The test verifies that an IllegalArgumentException is thrown when attempting to 
     * create such an answer for the question created in test 14.
     * </p>
     * 
     * @throws IllegalArgumentException If createAnswer is passed an illegal argument
     * 
     * @see QASystemTest#testCreateValidAnswer()
     */

    @Test
    @DisplayName("Test 15: Create answer with invalid content (too short)")
    void testCreateAnswerInvalidContentTooShort() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Answer(1, "Hi", testUser1.getUserName());
        }, "Should throw exception for answer less than 5 characters");
    }

    /**
     * <p> Test: Create answer with too many characters (Invalid) </p>
     * <p> 
     * Tests an invalid answer. In this test an invalid answer is one which has too many characters
     * (more than 2000 characters). The test verifies that an IllegalArgumentException is thrown when attempting to 
     * create such an answer for the question created in test 14.
     * </p>
     * 
     * @throws IllegalArgumentException If createAnswer is passed an illegal argument
     * 
     * @see QASystemTest#testCreateValidAnswer()
     */

    @Test
    @DisplayName("Test 16: Create answer with invalid content (too long)")
    void testCreateAnswerInvalidContentTooLong() {
        String longContent = "A".repeat(2001);
        assertThrows(IllegalArgumentException.class, () -> {
            new Answer(1, longContent, testUser1.getUserName());
        }, "Should throw exception for answer over 2000 characters");
    }

    /**
     * <p> Test: Create answer with no content (Invalid) </p>
     * <p> 
     * Tests an invalid answer. In this test an invalid answer is one which contains no characters
     * (under minimum necessary characters). This test verifies that an IllegalArgumentException is thrown when attempting to 
     * create such an answer for the question created in test 14.
     * </p>
     * 
     * @throws IllegalArgumentException If createAnswer is passed an illegal argument
     * 
     * @see QASystemTest#testCreateValidAnswer()
     */

    @Test
    @DisplayName("Test 17: Create answer with empty content")
    void testCreateAnswerEmptyContent() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Answer(1, "", testUser1.getUserName());
        }, "Should throw exception for empty answer");
    }

    /**
     * <p> Test: Read answers for a question </p>
     * <p> 
     * This test verifies that the correct answers are returned when attempting to read the answers for
     * a question. This is verified by checking that the actual size of an Answer list for a particular question 
     * matches the expected size.
     * </p>
     * 
     * @throws SQLException If a database error occurs
     * 
     * @see QASystemTest#testCreateValidQuestion(), QASystemTest#testCreateValidAnswer()
     */

    @Test
    @DisplayName("Test 18: Read answers for a question")
    void testReadAnswersForQuestion() throws SQLException {
        Question q = new Question(
            "Question with Multiple Answers",
            "This will have several answers",
            testUser1.getUserName()
        );
        int qId = db.createQuestion(q);
        Answer a1 = new Answer(qId, "First answer here", testUser1.getUserName());
        Answer a2 = new Answer(qId, "Second answer here", testUser2.getUserName());
        db.createAnswer(a1);
        db.createAnswer(a2);
        List<Answer> answers = db.getAnswersForQuestion(qId);
        assertEquals(2, answers.size(), "Should have 2 answers");
    }

	/**
     * <p> Test: Update answer content </p>
     * <p> 
     * This test verifies that the content of an answer can be changed after the creation of the answer. 
     * This is verified by checking that the database method returns true, and checking that the actual content 
     * of the answer after updating it matches the expected.
     * </p>
     * 
     * @throws SQLException If a database error occurs
     */
    @Test
    @DisplayName("Test 19: Update answer content")
    void testUpdateAnswer() throws SQLException {
        Question q = new Question(
            "Question for Update Test",
            "Testing answer updates",
            testUser1.getUserName()
        );
        int qId = db.createQuestion(q);
        Answer a = new Answer(qId, "Original answer content", testUser2.getUserName());
        int aId = db.createAnswer(a);
        a.setContent("Updated answer content with more details");
        boolean updated = db.updateAnswer(a);
        assertTrue(updated, "Update should succeed");
        List<Answer> answers = db.getAnswersForQuestion(qId);
        Answer retrieved = answers.stream()
            .filter(ans -> ans.getId() == aId)
            .findFirst()
            .orElse(null);
        assertNotNull(retrieved, "Answer should exist");
        assertEquals("Updated answer content with more details", retrieved.getContent(), "Content should be updated");
    }

	/**
     * <p> Test: Delete answer </p>
     * <p> 
     * This test verifies that an answer can be deleted. This is verified by checking that the database method returns true 
     * and that the answer id no longer exists after deletion.
     * </p>
     * 
     * @throws SQLException If a database error occurs
     */
    @Test
    @DisplayName("Test 20: Delete answer")
    void testDeleteAnswer() throws SQLException {
        Question q = new Question(
            "Question for Delete Test",
            "Testing answer deletion",
            testUser1.getUserName()
        );
        int qId = db.createQuestion(q);
        Answer a = new Answer(qId, "Answer to be deleted", testUser2.getUserName());
        int aId = db.createAnswer(a);
        boolean deleted = db.deleteAnswer(aId, testUser2.getUserName());
        assertTrue(deleted, "Deletion should succeed");
        List<Answer> answers = db.getAnswersForQuestion(qId);
        boolean exists = answers.stream().anyMatch(ans -> ans.getId() == aId);
        assertFalse(exists, "Deleted answer should not exist");
    }

	/**
     * <p> Test: Mark answer as read </p>
     * <p> 
     * This test verifies that an answer can be marked as read. This is verified by checking that the database method 
     * returns true and that after updating isRead its value equals true.
     * </p>
     * 
     * @throws SQLException If a database error occurs
     */
    @Test
    @DisplayName("Test 21: Mark answer as read")
    void testMarkAnswerAsRead() throws SQLException {
        Question q = new Question(
            "Question for Read Test",
            "Testing mark as read",
            testUser1.getUserName()
        );
        int qId = db.createQuestion(q);
        Answer a = new Answer(qId, "Unread answer", testUser2.getUserName());
        int aId = db.createAnswer(a);
        assertFalse(a.isRead(), "Answer should initially be unread");
        boolean marked = db.markAnswerAsRead(aId);
        assertTrue(marked, "Mark as read should succeed");
        List<Answer> answers = db.getAnswersForQuestion(qId);
        Answer retrieved = answers.stream()
            .filter(ans -> ans.getId() == aId)
            .findFirst()
            .orElse(null);
        assertNotNull(retrieved, "Answer should exist");
        assertTrue(retrieved.isRead(), "Answer should be marked as read");
    }

    // ========== Question List Tests ==========

	/**
     * <p>Test: Get all questions for user</p>
     * <p>
     * This test verifies that the correct questions are returned when getting a list 
     * of question belonging to a user. This is verified checking that the list of questions 
     * returned is at least 2 and that the askedBy value equals the user is should.
     * </p>
     * 
     * @throws SQLException If a database error occurs.
     */
    @Test
    @DisplayName("Test 22: Get all questions for user")
    void testGetAllQuestionsForUser() throws SQLException {
        Question q1 = new Question("User1 Q1", "Content 1 here", testUser1.getUserName());
        Question q2 = new Question("User1 Q2", "Content 2 here", testUser1.getUserName());
        db.createQuestion(q1);
        db.createQuestion(q2);
        List<Question> questions = db.getAllQuestions(testUser1.getUserName());
        assertTrue(questions.size() >= 2, "Should have at least 2 questions for user1");
        boolean allMatchUser = questions.stream()
            .allMatch(q -> q.getAskedBy().equals(testUser1.getUserName()));
        assertTrue(allMatchUser, "All questions should belong to user1");
    }

	/**
     * <p>Test: Get unresolved questions only</p>
     * <p>
     * This test verifies that only unresolved questions are returned when searching for unresolved 
     * questions. This is verified by checking the list that's returned for any questions where 
     * isResolved is true, if there are none the test passes.
     * </p>
     * 
     * @throws SQLException If a database error occurs.
     */
    @Test
    @DisplayName("Test 23: Get unresolved questions only")
    void testGetUnresolvedQuestions() throws SQLException {
        Question q = new Question(
            "Unresolved Question",
            "This is unresolved",
            testUser1.getUserName()
        );
        db.createQuestion(q);
        List<Question> unresolved = db.getUnresolvedQuestions();
        assertTrue(unresolved.stream().noneMatch(Question::isResolved),
                   "All questions should be unresolved");
    }

	/**
     * <p>Test: Search questions by keyword</p>
     * <p>
     * This test verifies that when searching by keyword only results containing the keyword are shown.
     * This is verified by checking the list that's returned for the desired keyword both in the 
     * title and in the content. If there is any question returned that doesn't contain the keyword 
     * the test fails, otherwise it succeeds.
     * </p>
     * 
     * @throws SQLException If a database error occurs.
     */
    @Test
    @DisplayName("Test 24: Search questions by keyword")
    void testSearchQuestions() throws SQLException {
        Question q = new Question(
            "How to use recursion in Java?",
            "I need help understanding recursive functions",
            testUser1.getUserName()
        );
        db.createQuestion(q);
        List<Question> results = db.searchQuestions("recursion");
        assertTrue(results.size() > 0, "Should find questions with 'recursion'");
        assertTrue(results.stream()
            .anyMatch(qu -> qu.getTitle().toLowerCase().contains("recursion") ||
                           qu.getContent().toLowerCase().contains("recursion")),
            "Results should contain keyword");
    }

	/**
     * <p>Test: Mark question as resolved with specific answer</p>
     * <p>
     * This test verifies that a question can be marked as resolved and that a specific answer 
     * can be marked as the answer that resolved the question. This is verified by checking that the 
     * return value is true when marking a question as resolved and when reading the isResolved value.
     * The test also checks that the correct answer id is retrieved from the question when reading the 
     * questions resolvedAnswerId value.
     * </p>
     * 
     * @throws SQLException If a database error occurs.
     */
    @Test
    @DisplayName("Test 25: Mark question as resolved with specific answer")
    void testMarkQuestionResolved() throws SQLException {
        Question q = new Question(
            "Question to Resolve",
            "This will be marked resolved",
            testUser1.getUserName()
        );
        int qId = db.createQuestion(q);
        Answer a = new Answer(qId, "This is the solution", testUser2.getUserName());
        int aId = db.createAnswer(a);
        boolean marked = db.markQuestionResolved(qId, aId, testUser1.getUserName());
        assertTrue(marked, "Should successfully mark as resolved");
        Question retrieved = db.getQuestionById(qId);
        assertTrue(retrieved.isResolved(), "Question should be resolved");
        assertEquals(aId, retrieved.getResolvedAnswerId(), "Should have correct resolved answer ID");
    }
        /**
         * Verifies that the unread answer count for a question is accurate and updates.
         * <p>
         * This test:
         * <br>1. Creates a question and two new answers for it
         * <br>2. Asserts this question's unread answer count is set to two
         * <br>3. Marks one answer as read
         * <br>4. Asserts this question's unread answer count is updated to one
         *
         * @throws SQLException if a database access error occurs.
         */
        @Test
        @DisplayName("Test 26: Count unread answers for question owner")
        void testUnreadAnswerCount() throws SQLException {
            Question q = new Question(
                "Question with Unread Answers",
                "Testing unread count",
                testUser1.getUserName()
            );
            int qId = db.createQuestion(q);
            Answer a1 = new Answer(qId, "Unread answer 1", testUser2.getUserName());
            Answer a2 = new Answer(qId, "Unread answer 2", testUser2.getUserName());
            db.createAnswer(a1);
            db.createAnswer(a2);
            Question retrieved = db.getQuestionById(qId);
            assertEquals(2, retrieved.getUnreadAnswerCount(), "Should have 2 unread answers");
            db.markAnswerAsRead(a1.getId());
            retrieved = db.getQuestionById(qId);
            assertEquals(1, retrieved.getUnreadAnswerCount(), "Should have 1 unread answer");
        }

        /**
         * Tests that deleting a question deletes all of its answers.
         * <p>
         * This test ensures that question deletion is working correctly. 
         * <br>This test:
         * <br>1. Creates a question
         * <br>2. Adds an answer to this question
         * <br>3. Deletes this question
         * <br>4. Asserts this question's answers no longer exist
         *
         * @throws SQLException if a database access error occurs.
         */
        @Test
        @DisplayName("Test 27: Question deletion cascades to answers")
        void testQuestionDeletionCascade() throws SQLException {
            Question q = new Question(
                "Question to Delete with Answers",
                "This and its answers will be deleted",
                testUser1.getUserName()
            );
            int qId = db.createQuestion(q);
            Answer a = new Answer(qId, "This answer will be deleted too", testUser2.getUserName());
            int aId = db.createAnswer(a);
            db.deleteQuestion(qId, testUser1.getUserName());
            List<Answer> answers = db.getAnswersForQuestion(qId);
            assertEquals(0, answers.size(), "Answers should be deleted with question");
        }

        /**
         * Verifies that the Question constructor throws an {@link IllegalArgumentException}
         * when provided with a null title.
         * <p>
         * This tests the question's input validation, for null title.
         */
        @Test
        @DisplayName("Test 28: Question validation - null title")
        void testQuestionValidationNullTitle() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Question(null, "Valid content here", testUser1.getUserName());
            }, "Should throw exception for null title");
        }

        /**
         * Verifies that the Answer constructor throws an {@link IllegalArgumentException}
         * when provided with null content.
         * <p>
         * This tests the answer's input validation, for null content.
         */
        @Test
        @DisplayName("Test 29: Answer validation - null content")
        void testAnswerValidationNullContent() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Answer(1, null, testUser1.getUserName());
            }, "Should throw exception for null content");
        }

        /**
         * Tests the functionality of closing a question without selecting a specific answer.
         * <p>
         * This test verifies that a question owner can mark their question as
         * "resolved" (they found the solution themselves) without accepting
         * any particular answer. <br>It asserts that the question's state is
         * correctly updated to 'resolved' and that no specific answer is set.
         *<br>This test:
         *<br>1. Creates a question
         *<br>2. Creates two answers
         *<br>3. Closes this question, asserting this question is resolved with no selected correct answer
         * @throws SQLException if a database access error occurs.
         */
        @Test
        @DisplayName("Test 30: Close question without specifying answer")
        void testCloseQuestion() throws SQLException {
            Question q = new Question(
                "Question to Close",
                "This will be closed without specific answer",
                testUser1.getUserName()
            );
            int qId = db.createQuestion(q);
            Answer a1 = new Answer(qId, "First answer", testUser2.getUserName());
            Answer a2 = new Answer(qId, "Second answer", testUser2.getUserName());
            db.createAnswer(a1);
            db.createAnswer(a2);
            boolean closed = db.closeQuestion(qId, testUser1.getUserName());
            assertTrue(closed, "Should successfully close question");
            Question retrieved = db.getQuestionById(qId);
            assertTrue(retrieved.isResolved(), "Question should be marked as resolved");
            assertEquals(-1, retrieved.getResolvedAnswerId(), "Should not have specific resolved answer");
        }
        
        /**

         * Test suite for Staff role functionality

         */


        /**

         * <p>Test: Staff can view all questions</p>

         * <p>

         * Verifies that staff members can access and review all questions in the system,

         * regardless of who created them or their resolution status.

         * </p>

         * 

         * @throws SQLException If database error occurs

         */

        @Test

        @DisplayName("Test 31: Staff can view all questions")

        void testStaffCanViewAllQuestions() throws SQLException {

            // Create test questions from different users

            Question q1 = new Question("Test Question 1", "Content for question 1", testUser1.getUserName());

            Question q2 = new Question("Test Question 2", "Content for question 2", testUser2.getUserName());

            

            int id1 = db.createQuestion(q1);

            int id2 = db.createQuestion(q2);

            

            // Staff should be able to retrieve all questions

            List<Question> allQuestions = db.getAllQuestions(null);

            

            assertTrue(allQuestions.size() >= 2, "Staff should see all questions");

            assertTrue(allQuestions.stream().anyMatch(q -> q.getId() == id1), "Should include question 1");

            assertTrue(allQuestions.stream().anyMatch(q -> q.getId() == id2), "Should include question 2");

        }


        /**

         * <p>Test: Staff can view all answers</p>

         * <p>

         * Verifies that staff members can access and review all answers across all questions,

         * enabling them to monitor the quality and appropriateness of responses.

         * </p>

         * 

         * @throws SQLException If database error occurs

         */

        @Test

        @DisplayName("Test 32: Staff can view all answers")

        void testStaffCanViewAllAnswers() throws SQLException {

            Question q = new Question("Test Question", "Test content", testUser1.getUserName());

            int qId = db.createQuestion(q);

            

            Answer a1 = new Answer(qId, "First answer content", testUser1.getUserName());

            Answer a2 = new Answer(qId, "Second answer content", testUser2.getUserName());

            

            db.createAnswer(a1);

            db.createAnswer(a2);

            

            List<Answer> answers = db.getAnswersForQuestion(qId);

            

            assertEquals(2, answers.size(), "Staff should see all answers");

        }


        /**

         * <p>Test: Staff can view private feedback</p>

         * <p>

         * Verifies that staff members have access to private feedback messages,

         * allowing them to monitor interactions and identify potential issues.

         * </p>

         * 

         * @throws SQLException If database error occurs

         */

        @Test

        @DisplayName("Test 33: Staff can view private feedback")

        void testStaffCanViewPrivateFeedback() throws SQLException {

            Question q = new Question("Test Question", "Test content", testUser1.getUserName());

            int qId = db.createQuestion(q);

            

            // Add private message

            db.addPrivateMessage(qId, testUser2.getUserName(), testUser1.getUserName(), "QUESTION", "Private feedback content");

            

            List<DatabaseHelper.PrivateMessage> messages = db.getPrivateMessagesForQuestion(qId);

            

            assertFalse(messages.isEmpty(), "Staff should see private messages");

            assertEquals(1, messages.size(), "Should have one private message");

        }


        /**

         * <p>Test: Staff can access unresolved questions</p>

         * <p>

         * Verifies that staff can specifically view unresolved questions to identify

         * areas where students may need additional support.

         * </p>

         * 

         * @throws SQLException If database error occurs

         */

        @Test

        @DisplayName("Test 34: Staff can identify unresolved questions")

        void testStaffCanIdentifyUnresolvedQuestions() throws SQLException {

            Question q1 = new Question("Unresolved Question", "Need help with this", testUser1.getUserName());

            Question q2 = new Question("Resolved Question", "This was resolved", testUser1.getUserName());

            

            int id1 = db.createQuestion(q1);

            int id2 = db.createQuestion(q2);

            

            // Resolve second question

            Answer a = new Answer(id2, "Solution answer", testUser2.getUserName());

            int aId = db.createAnswer(a);

            db.markQuestionResolved(id2, aId, testUser1.getUserName());

            

            List<Question> unresolved = db.getUnresolvedQuestions();

            

            assertTrue(unresolved.stream().anyMatch(q -> q.getId() == id1), "Should include unresolved question");

            assertFalse(unresolved.stream().anyMatch(q -> q.getId() == id2), "Should not include resolved question");

        }


        /**

         * <p>Test: Staff can view question-answer relationships</p>

         * <p>

         * Verifies that staff can properly track which answers belong to which questions,

         * enabling comprehensive review of question threads.

         * </p>

         * 

         * @throws SQLException If database error occurs

         */

        @Test

        @DisplayName("Test 35: Staff can track question-answer relationships")

        void testStaffCanTrackQuestionAnswerRelationships() throws SQLException {

            Question q = new Question("Parent Question", "Question content", testUser1.getUserName());

            int qId = db.createQuestion(q);

            

            Answer a1 = new Answer(qId, "Answer 1", testUser1.getUserName());

            Answer a2 = new Answer(qId, "Answer 2", testUser2.getUserName());

            

            db.createAnswer(a1);

            db.createAnswer(a2);

            

            Question retrieved = db.getQuestionById(qId);

            List<Answer> answers = db.getAnswersForQuestion(qId);

            

            assertNotNull(retrieved, "Should retrieve question");

            assertEquals(2, answers.size(), "Should have 2 answers");

            

            for (Answer answer : answers) {

                assertEquals(qId, answer.getQuestionId(), "Answer should link to correct question");

            }

        }


        /**
         * <p>Test: Staff role assignment</p>
         * <p>
         * Verifies that users can be properly assigned the Staff role through the
         * admin interface.
         * </p>
         * 
         * @throws SQLException If database error occurs
         */
        @Test
        @DisplayName("Test 36: Staff role can be assigned")
        void testStaffRoleAssignment() throws SQLException {
            // Check if user already exists, if so delete first
            if (db.doesUserExist("staffUser")) {
                // First remove any data that references this user
                try {
                    // Get all questions by this user and delete them
                    List<Question> userQuestions = db.getAllQuestions("staffUser");
                    for (Question q : userQuestions) {
                        db.deleteQuestion(q.getId(), "staffUser");
                    }
                } catch (Exception e) {
                    // Ignore if no questions exist
                }
                db.deleteUser("staffUser");
            }
            
            // Create new staff user with all required fields
            User testStaff = new User("staffUser", "Pass123!", "Student");
            testStaff.setEmail("staff@test.edu");
            testStaff.setFirstName("Staff");
            testStaff.setLastName("User");
            testStaff.setMiddleInitial("S");
            
            db.register(testStaff);
            
            // Admin changes role to Staff
            boolean updated = db.updateUserRole("staffUser", "Staff");
            
            assertTrue(updated, "Role update should succeed");
            
            String newRole = db.getUserRole("staffUser");
            assertEquals("Staff", newRole, "User should have Staff role");
        }


        /**
         * <p>Test: Staff can view all reviews</p>
         * <p>
         * Verifies that staff members can access and review all reviews posted by reviewers,
         * enabling monitoring of review quality.
         * </p>
         * 
         * @throws SQLException If database error occurs
         */
        @Test
        @DisplayName("Test 37: Staff can view all reviews")
        void testStaffCanViewReviews() throws SQLException {
            Question q = new Question("Review Test Question", "Test content for review", testUser1.getUserName());
            int qId = db.createQuestion(q);
            
            Answer a = new Answer(qId, "Test answer content for review", testUser2.getUserName());
            int aId = db.createAnswer(a);
            
            // Create a staff user with all required fields
            if (!db.doesUserExist("reviewStaff")) {
                User staffUser = new User("reviewStaff", "Pass123!", "Staff");
                staffUser.setEmail("reviewstaff@test.edu");
                staffUser.setFirstName("Review");
                staffUser.setLastName("Staff");
                staffUser.setMiddleInitial("R");
                db.register(staffUser);
            }
            
            db.addReview(aId, "reviewStaff", "This is a helpful and well-researched answer");
            
            List<Review> reviews = db.getAllReviews(null);
            assertTrue(reviews.stream().anyMatch(r -> r.getAnswerId() == aId), "Should find review for answer");
        }


        /**
         * <p>Test: Staff monitoring statistics</p>
         * <p>
         * Verifies that staff can access statistical information about system usage
         * to identify trends and potential issues.
         * </p>
         * 
         * @throws SQLException If database error occurs
         */
        @Test
        @DisplayName("Test 38: Staff can access system statistics")
        void testStaffCanAccessStatistics() throws SQLException {
            // Use existing test users instead of creating new ones
            Question q1 = new Question("Stats Question 1", "Content for stats 1", testUser1.getUserName());
            Question q2 = new Question("Stats Question 2", "Content for stats 2", testUser2.getUserName());
            
            int qId1 = db.createQuestion(q1);
            int qId2 = db.createQuestion(q2);
            
            Answer a1 = new Answer(qId1, "Answer for stats 1", testUser1.getUserName());
            Answer a2 = new Answer(qId2, "Answer for stats 2", testUser2.getUserName());
            
            db.createAnswer(a1);
            db.createAnswer(a2);
            
            // Staff should be able to count questions and answers
            List<Question> allQuestions = db.getAllQuestions(null);
            int totalAnswers = 0;
            for (Question q : allQuestions) {
                totalAnswers += db.getAnswersForQuestion(q.getId()).size();
            }
            
            assertTrue(allQuestions.size() >= 2, "Should have at least 2 questions");
            assertTrue(totalAnswers >= 2, "Should have at least 2 answers");
        }


        /**

         * <p>Test: Staff can identify high-activity questions</p>

         * <p>

         * Verifies that staff can identify questions with high engagement

         * (many answers or interactions) that may need special attention.

         * </p>

         * 

         * @throws SQLException If database error occurs

         */

        @Test

        @DisplayName("Test 39: Staff can identify high-activity questions")

        void testStaffCanIdentifyHighActivityQuestions() throws SQLException {

            Question q = new Question("Popular Question", "Lots of interest in this", testUser1.getUserName());

            int qId = db.createQuestion(q);

            

            // Add multiple answers

            for (int i = 0; i < 5; i++) {

                Answer a = new Answer(qId, "Answer " + i, testUser1.getUserName());

                db.createAnswer(a);

            }

            

            Question retrieved = db.getQuestionById(qId);

            List<Answer> answers = db.getAnswersForQuestion(qId);

            

            assertNotNull(retrieved, "Should retrieve question");

            assertEquals(5, answers.size(), "Should have 5 answers");

            assertTrue(answers.size() >= 5, "High activity question should have many answers");

        }


        /**

         * <p>Test: Staff can monitor private message volume</p>

         * <p>

         * Verifies that staff can track the volume of private feedback being exchanged,

         * which may indicate engagement or potential issues.

         * </p>

         * 

         * @throws SQLException If database error occurs

         */

        @Test

        @DisplayName("Test 40: Staff can monitor private message activity")

        void testStaffCanMonitorPrivateMessageActivity() throws SQLException {

            Question q = new Question("Question with feedback", "Test content", testUser1.getUserName());

            int qId = db.createQuestion(q);

            

            // Add multiple private messages

            db.addPrivateMessage(qId, testUser2.getUserName(), testUser1.getUserName(), "QUESTION", "Feedback 1");

            db.addPrivateMessage(qId, testUser1.getUserName(), testUser2.getUserName(), "ANSWER", "Response 1");

            db.addPrivateMessage(qId, testUser2.getUserName(), testUser1.getUserName(), "QUESTION", "Feedback 2");

            

            List<DatabaseHelper.PrivateMessage> messages = db.getPrivateMessagesForQuestion(qId);

            

            assertEquals(3, messages.size(), "Should have 3 private messages");

            assertTrue(messages.size() >= 3, "Staff should see active private communication");

        }
        
        /**
         * <p>Test: Staff can flag questions</p>
         * <p>
         * Verifies that staff members can flag questions for instructor review
         * and that the flag is properly stored in the database.
         * </p>
         * 
         * @throws SQLException If database error occurs
         */
        @Test
        @DisplayName("Test 41: Staff can flag questions")
        void testStaffCanFlagQuestions() throws SQLException {
            Question q = new Question("Questionable content question", "This might need review", testUser1.getUserName());
            int qId = db.createQuestion(q);
            
            // Create a staff user with all required fields
            if (!db.doesUserExist("flagStaff")) {
                User staffUser = new User("flagStaff", "Pass123!", "Staff");
                staffUser.setEmail("flagstaff@test.edu");
                staffUser.setFirstName("Flag");
                staffUser.setLastName("Staff");
                staffUser.setMiddleInitial("F");
                db.register(staffUser);
            }
            
            int flagId = db.addStaffFlag("QUESTION", qId, "flagStaff", "Inappropriate language");
            
            assertTrue(flagId > 0, "Flag ID should be positive");
            
            List<DatabaseHelper.StaffFlag> flags = db.getOpenStaffFlags();
            assertTrue(flags.stream().anyMatch(f -> f.getItemId() == qId), "Should find the flagged question");
        }


        /**
         * <p>Test: Staff can add notes to questions</p>
         * <p>
         * Verifies that staff members can add internal notes to questions
         * for tracking and communication purposes.
         * </p>
         * 
         * @throws SQLException If database error occurs
         */
        @Test
        @DisplayName("Test 42: Staff can add notes to questions")
        void testStaffCanAddNotes() throws SQLException {
            Question q = new Question("Note Test Question", "Test content for notes", testUser1.getUserName());
            int qId = db.createQuestion(q);
            
            // Create a staff user with all required fields
            if (!db.doesUserExist("noteStaff")) {
                User staffUser = new User("noteStaff", "Pass123!", "Staff");
                staffUser.setEmail("notestaff@test.edu");
                staffUser.setFirstName("Note");
                staffUser.setLastName("Staff");
                staffUser.setMiddleInitial("N");
                db.register(staffUser);
            }
            
            int noteId = db.addStaffNote(qId, "This question needs monitoring", "noteStaff");
            
            assertTrue(noteId > 0, "Note ID should be positive");
            
            List<DatabaseHelper.StaffNote> notes = db.getStaffNotes(qId);
            assertEquals(1, notes.size(), "Should have one note");
            assertEquals("This question needs monitoring", notes.get(0).getNoteText(), "Note text should match");
        }


        /**
         * <p>Test: Staff can post reviews on answers</p>
         * <p>
         * Verifies that staff members can post reviews on answers
         * to help guide students toward quality responses.
         * </p>
         * 
         * @throws SQLException If database error occurs
         */
        @Test
        @DisplayName("Test 43: Staff can post reviews on answers")
        void testStaffCanPostReviews() throws SQLException {
            Question q = new Question("Post Review Test Question", "Test content for post review", testUser1.getUserName());
            int qId = db.createQuestion(q);
            
            Answer a = new Answer(qId, "Test answer content for posting review", testUser2.getUserName());
            int aId = db.createAnswer(a);
            
            // Create a staff user with all required fields
            if (!db.doesUserExist("postStaff")) {
                User staffUser = new User("postStaff", "Pass123!", "Staff");
                staffUser.setEmail("poststaff@test.edu");
                staffUser.setFirstName("Post");
                staffUser.setLastName("Staff");
                staffUser.setMiddleInitial("P");
                db.register(staffUser);
            }
            
            db.addReview(aId, "postStaff", "This is a helpful and well-researched answer");
            
            List<Review> reviews = db.getAllReviews(null);
            assertTrue(reviews.stream().anyMatch(r -> r.getAnswerId() == aId), "Should find review for answer");
        }
    }
   
