package application;

import java.sql.SQLException;
import java.util.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import databasePart1.DatabaseHelper;
import databasePart1.DatabaseHelper.PrivateMessage;

/**
 * StaffHomePage provides the interface for staff members to review and monitor
 * interactions within the Q&A system. Staff members can assess questions, answers,
 * and private feedback to help identify potential issues early.
 * 
 * @author HW4 Implementation
 * @version 1.0
 */
public class StaffHomePage {
    private final DatabaseHelper databaseHelper;
    private final User currentUser;
    private Stage primaryStage;
    private VBox mainLayout;
    private TabPane tabPane;
    
    /**
     * Constructs a new StaffHomePage with the given database helper and current user.
     * 
     * @param databaseHelper The database helper for data operations
     * @param currentUser The currently logged-in staff member
     */
    public StaffHomePage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }
    
    /**
     * Displays the staff home page interface in the primary stage.
     * 
     * @param primaryStage The primary stage where the scene will be displayed
     */
    public void show(Stage primaryStage) {
        this.primaryStage = primaryStage;
        mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");
        
        // Header
        Label titleLabel = new Label("Staff Review Dashboard");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label welcomeLabel = new Label("Welcome, " + currentUser.getUserName() + " (Staff)");
        welcomeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        // Create tab pane for different review sections
        tabPane = new TabPane();
        tabPane.setMinHeight(500);
        
        Tab overviewTab = createOverviewTab();
        Tab questionsTab = createQuestionsReviewTab();
        Tab answersTab = createAnswersReviewTab();
        Tab reviewsTab = createReviewsTab();
        Tab feedbackTab = createFeedbackReviewTab();
        Tab alertsTab = createAlertsTab();
        
        tabPane.getTabs().addAll(overviewTab, questionsTab, answersTab, reviewsTab, feedbackTab, alertsTab);
        
        // Bottom buttons
        Button backButton = new Button("Back to Home");
        backButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 8 16;");
        backButton.setOnAction(e -> {
            new WelcomeLoginPage(databaseHelper).show(primaryStage, currentUser);
        });
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 16;");
        refreshButton.setOnAction(e -> refreshAllTabs());
        
        HBox buttonBox = new HBox(10, backButton, refreshButton);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        
        mainLayout.getChildren().addAll(titleLabel, welcomeLabel, tabPane, buttonBox);
        
        ScrollPane scrollPane = new ScrollPane(mainLayout);
        scrollPane.setFitToWidth(true);
        Scene scene = new Scene(scrollPane, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("sQaaS™ - Staff Dashboard");
        primaryStage.show();
    }
    
    /**
     * Creates the Overview tab showing system-wide statistics and trends.
     * 
     * @return Tab containing the overview interface
     */
    private Tab createOverviewTab() {
        Tab tab = new Tab("Overview");
        tab.setClosable(false);
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");
        
        Label sectionLabel = new Label("System Overview");
        sectionLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Statistics grid
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(15);
        statsGrid.setPadding(new Insets(10));
        
        try {
            // Get statistics
            List<Question> allQuestions = databaseHelper.getAllQuestions(null);
            List<Question> unresolvedQuestions = databaseHelper.getUnresolvedQuestions();
            
            int totalAnswers = 0;
            int totalPrivateMessages = 0;
            int totalReviews = 0;
            
            for (Question q : allQuestions) {
                totalAnswers += databaseHelper.getAnswersForQuestion(q.getId()).size();
                totalPrivateMessages += databaseHelper.getPrivateMessagesForQuestion(q.getId()).size();
            }
            
            totalReviews = databaseHelper.getAllReviews(null).size();
            
            // Display statistics
            addStatCard(statsGrid, "Total Questions", String.valueOf(allQuestions.size()), 0, 0);
            addStatCard(statsGrid, "Unresolved Questions", String.valueOf(unresolvedQuestions.size()), 1, 0);
            addStatCard(statsGrid, "Total Answers", String.valueOf(totalAnswers), 0, 1);
            addStatCard(statsGrid, "Private Messages", String.valueOf(totalPrivateMessages), 1, 1);
            addStatCard(statsGrid, "Reviews Posted", String.valueOf(totalReviews), 0, 2);
            
            // Calculate resolution rate
            double resolutionRate = allQuestions.size() > 0 ? 
                ((double)(allQuestions.size() - unresolvedQuestions.size()) / allQuestions.size() * 100) : 0;
            addStatCard(statsGrid, "Resolution Rate", String.format("%.1f%%", resolutionRate), 1, 2);
            
        } catch (SQLException e) {
            Label errorLabel = new Label("Error loading statistics: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            content.getChildren().add(errorLabel);
        }
        
        // Recent activity section
        Label activityLabel = new Label("Recent Activity Highlights");
        activityLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");
        
        VBox activityBox = new VBox(10);
        activityBox.setPadding(new Insets(10));
        activityBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-background-color: #fafafa;");
        
        try {
            List<Question> recentQuestions = databaseHelper.getAllQuestions(null);
            if (recentQuestions.size() > 5) {
                recentQuestions = recentQuestions.subList(0, 5);
            }
            
            for (Question q : recentQuestions) {
                Label activityItem = new Label("• " + q.getTitle() + " by " + q.getAskedBy() + 
                    " - " + q.getFormattedDate());
                activityItem.setWrapText(true);
                activityBox.getChildren().add(activityItem);
            }
            
            if (recentQuestions.isEmpty()) {
                activityBox.getChildren().add(new Label("No recent activity"));
            }
            
        } catch (SQLException e) {
            Label errorLabel = new Label("Error loading recent activity");
            errorLabel.setStyle("-fx-text-fill: red;");
            activityBox.getChildren().add(errorLabel);
        }
        
        content.getChildren().addAll(sectionLabel, statsGrid, activityLabel, activityBox);
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        tab.setContent(scrollPane);
        
        return tab;
    }
    
    /**
     * Helper method to add a statistic card to the grid.
     * 
     * @param grid The GridPane to add the card to
     * @param label The label for the statistic
     * @param value The value to display
     * @param col The column position
     * @param row The row position
     */
    private void addStatCard(GridPane grid, String label, String value, int col, int row) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 5;");
        card.setPrefWidth(200);
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label titleLabel = new Label(label);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        card.getChildren().addAll(valueLabel, titleLabel);
        grid.add(card, col, row);
    }
    
    /**
     * Creates the Questions Review tab for reviewing all questions in the system.
     * 
     * @return Tab containing the questions review interface
     */
    private Tab createQuestionsReviewTab() {
        Tab tab = new Tab("Questions Review");
        tab.setClosable(false);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("Question Review");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Filter options
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label filterLabel = new Label("Filter:");
        ComboBox<String> filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll("All Questions", "Unresolved Only", "Resolved Only", "Flagged");
        filterCombo.setValue("All Questions");
        
        Button applyFilterButton = new Button("Apply");
        applyFilterButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        
        filterBox.getChildren().addAll(filterLabel, filterCombo, applyFilterButton);
        
        // Questions display area
        VBox questionsDisplay = new VBox(10);
        
        // Load questions
        Runnable loadQuestions = () -> {
            questionsDisplay.getChildren().clear();
            try {
                List<Question> questions;
                String filter = filterCombo.getValue();
                
                if ("Unresolved Only".equals(filter)) {
                    questions = databaseHelper.getUnresolvedQuestions();
                } else if ("Resolved Only".equals(filter)) {
                    questions = databaseHelper.getAllQuestions(null);
                    questions.removeIf(q -> !q.isResolved());
                } else {
                    questions = databaseHelper.getAllQuestions(null);
                }
                
                if (questions.isEmpty()) {
                    questionsDisplay.getChildren().add(new Label("No questions to display"));
                } else {
                    for (Question q : questions) {
                        questionsDisplay.getChildren().add(createQuestionReviewCard(q));
                    }
                }
                
            } catch (SQLException e) {
                Label errorLabel = new Label("Error loading questions: " + e.getMessage());
                errorLabel.setStyle("-fx-text-fill: red;");
                questionsDisplay.getChildren().add(errorLabel);
            }
        };
        
        applyFilterButton.setOnAction(e -> loadQuestions.run());
        loadQuestions.run(); // Initial load
        
        content.getChildren().addAll(titleLabel, filterBox, new Separator(), questionsDisplay);
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        tab.setContent(scrollPane);
        
        return tab;
    }
    
    /**
     * Creates a review card for a single question.
     * 
     * @param question The question to create a card for
     * @return VBox containing the question review card
     */
    private VBox createQuestionReviewCard(Question question) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-background-color: #fafafa;");
        
        // Header with title and status
        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label titleLabel = new Label(question.getTitle());
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);
        
        Label statusLabel = new Label(question.isResolved() ? "✓ Resolved" : "○ Unresolved");
        statusLabel.setStyle(question.isResolved() ? 
            "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-padding: 3 8; -fx-font-size: 10px;" :
            "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 3 8; -fx-font-size: 10px;");
        
        header.getChildren().addAll(titleLabel, statusLabel);
        
        // Metadata
        Label metaLabel = new Label(String.format("Asked by: %s | Posted: %s | Answers: %d", 
            question.getAskedBy(), question.getFormattedDate(), question.getAnswers().size()));
        metaLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
        
        // Content preview
        String contentPreview = question.getContent().length() > 150 ? 
            question.getContent().substring(0, 150) + "..." : question.getContent();
        Label contentLabel = new Label(contentPreview);
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-font-size: 12px;");
        
        // Show staff notes if any
        try {
            List<DatabaseHelper.StaffNote> notes = databaseHelper.getStaffNotes(question.getId());
            if (!notes.isEmpty()) {
                VBox notesBox = new VBox(5);
                notesBox.setStyle("-fx-background-color: #fff3cd; -fx-padding: 8; -fx-border-color: #ffc107; -fx-border-width: 1;");
                Label notesHeader = new Label("📝 Staff Notes (" + notes.size() + "):");
                notesHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
                notesBox.getChildren().add(notesHeader);
                
                for (DatabaseHelper.StaffNote note : notes) {
                    Label noteLabel = new Label(note.getCreatedBy() + ": " + note.getNoteText());
                    noteLabel.setWrapText(true);
                    noteLabel.setStyle("-fx-font-size: 10px;");
                    notesBox.getChildren().add(noteLabel);
                }
                
                card.getChildren().add(notesBox);
            }
        } catch (SQLException e) {
            // Silently fail
        }
        
        // Action buttons
        HBox buttonBox = new HBox(10);
        
        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.setStyle("-fx-font-size: 11px; -fx-background-color: #3498db; -fx-text-fill: white;");
        viewDetailsButton.setOnAction(e -> showQuestionDetails(question));
        
        Button reviewAnswersButton = new Button("📝 Review Answers");
        reviewAnswersButton.setStyle("-fx-font-size: 11px; -fx-background-color: #9b59b6; -fx-text-fill: white;");
        reviewAnswersButton.setOnAction(e -> showAnswersForReview(question));
        
        Button flagButton = new Button("🚩 Flag");
        flagButton.setStyle("-fx-font-size: 11px;");
        flagButton.setOnAction(e -> flagQuestion(question));
        
        Button addNoteButton = new Button("Add Staff Note");
        addNoteButton.setStyle("-fx-font-size: 11px;");
        addNoteButton.setOnAction(e -> addStaffNote(question));
        
        buttonBox.getChildren().addAll(viewDetailsButton, reviewAnswersButton, flagButton, addNoteButton);
        
        card.getChildren().addAll(header, metaLabel, contentLabel, buttonBox);
        
        return card;
    }
    
    /**
     * Shows answers for a question so staff can review them.
     * 
     * @param question The question whose answers to review
     */
    private void showAnswersForReview(Question question) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Review Answers");
        dialog.setHeaderText("Select an answer to review for: " + question.getTitle());
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(700);
        
        try {
            List<Answer> answers = databaseHelper.getAnswersForQuestion(question.getId());
            
            if (answers.isEmpty()) {
                content.getChildren().add(new Label("No answers to review yet"));
            } else {
                for (Answer answer : answers) {
                    VBox answerBox = new VBox(10);
                    answerBox.setPadding(new Insets(15));
                    answerBox.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-background-color: #f8f9fa;");
                    
                    Label authorLabel = new Label("By: " + answer.getAnsweredBy() + " | Upvotes: " + answer.getUpvotes());
                    authorLabel.setStyle("-fx-font-weight: bold;");
                    
                    Label contentLabel = new Label(answer.getContent());
                    contentLabel.setWrapText(true);
                    
                    // Show existing reviews for this answer
                    try {
                        List<Review> existingReviews = databaseHelper.getReviewsByAnswerId(answer.getId());
                        if (!existingReviews.isEmpty()) {
                            Label reviewsLabel = new Label("Existing Reviews (" + existingReviews.size() + "):");
                            reviewsLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 5 0 0 0;");
                            answerBox.getChildren().add(reviewsLabel);
                            
                            for (Review rev : existingReviews) {
                                Label revLabel = new Label("• " + rev.getWrittenBy() + ": " + 
                                    (rev.getReviewText().length() > 50 ? rev.getReviewText().substring(0, 50) + "..." : rev.getReviewText()));
                                revLabel.setWrapText(true);
                                revLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: #666;");
                                answerBox.getChildren().add(revLabel);
                            }
                        }
                    } catch (SQLException e) {
                        // Silently fail
                    }
                    
                    Button reviewButton = new Button("✍️ Post Review");
                    reviewButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
                    reviewButton.setOnAction(e -> {
                        dialog.close();
                        postReviewOnAnswer(answer);
                    });
                    
                    answerBox.getChildren().addAll(authorLabel, contentLabel, reviewButton);
                    content.getChildren().add(answerBox);
                }
            }
        } catch (SQLException e) {
            Label errorLabel = new Label("Error loading answers: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            content.getChildren().add(errorLabel);
        }
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        
        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
    
    /**
     * Creates a dialog for staff to post a review on an answer.
     * 
     * @param answer The answer to review
     */
    private void postReviewOnAnswer(Answer answer) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Post Staff Review");
        dialog.setHeaderText("Write a review for this answer");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        // Show the answer being reviewed
        Label answerLabel = new Label("Answer by: " + answer.getAnsweredBy());
        answerLabel.setStyle("-fx-font-weight: bold;");
        
        Label answerContent = new Label(answer.getContent());
        answerContent.setWrapText(true);
        answerContent.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");
        answerContent.setMaxWidth(500);
        
        Label reviewLabel = new Label("Your Review:");
        reviewLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 0 0;");
        
        TextArea reviewArea = new TextArea();
        reviewArea.setPromptText("Write your review here (minimum 10 characters)...");
        reviewArea.setWrapText(true);
        reviewArea.setPrefRowCount(5);
        reviewArea.setMaxWidth(500);
        
        Label charCount = new Label("0 characters");
        charCount.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        
        reviewArea.textProperty().addListener((obs, old, newVal) -> {
            charCount.setText(newVal.length() + " characters");
        });
        
        content.getChildren().addAll(answerLabel, answerContent, reviewLabel, reviewArea, charCount);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return reviewArea.getText();
            }
            return null;
        });
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(reviewText -> {
            if (reviewText.trim().length() < 10) {
                showAlert("Invalid Review", "Review must be at least 10 characters long", AlertType.WARNING);
                return;
            }
            
            try {
                databaseHelper.addReview(answer.getId(), currentUser.getUserName(), reviewText.trim());
                showAlert("Success", "Review posted successfully!", AlertType.INFORMATION);
                refreshAllTabs();
            } catch (SQLException e) {
                showAlert("Error", "Failed to post review: " + e.getMessage(), AlertType.ERROR);
            }
        });
    }
    
    /**
     * Shows detailed view of a question including all answers and interactions.
     * 
     * @param question The question to display details for
     */
    private void showQuestionDetails(Question question) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Question Details - Staff Review");
        dialog.setHeaderText(question.getTitle());
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(700);
        
        // Question content
        Label contentLabel = new Label(question.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-font-size: 13px; -fx-padding: 10; -fx-background-color: #ecf0f1;");
        
        Label metaLabel = new Label(String.format("Asked by: %s | Posted: %s | Status: %s",
            question.getAskedBy(), question.getFormattedDate(), 
            question.isResolved() ? "Resolved" : "Unresolved"));
        metaLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
        
        content.getChildren().addAll(new Label("Question Content:"), contentLabel, metaLabel);
        
        // Answers section
        Label answersHeader = new Label("Answers (" + question.getAnswers().size() + "):");
        answersHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        content.getChildren().add(answersHeader);
        
        try {
            List<Answer> answers = databaseHelper.getAnswersForQuestion(question.getId());
            if (answers.isEmpty()) {
                content.getChildren().add(new Label("No answers yet"));
            } else {
                for (Answer answer : answers) {
                    VBox answerBox = new VBox(5);
                    answerBox.setPadding(new Insets(10));
                    answerBox.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-background-color: #f8f9fa;");
                    
                    Label answerMeta = new Label(String.format("By: %s | Posted: %s | Upvotes: %d",
                        answer.getAnsweredBy(), answer.getFormattedDate(), answer.getUpvotes()));
                    answerMeta.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
                    
                    Label answerContent = new Label(answer.getContent());
                    answerContent.setWrapText(true);
                    
                    answerBox.getChildren().addAll(answerMeta, answerContent);
                    content.getChildren().add(answerBox);
                }
            }
            
            // Private messages section
            Label pmHeader = new Label("Private Feedback:");
            pmHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");
            content.getChildren().add(pmHeader);
            
            List<PrivateMessage> privateMessages = databaseHelper.getPrivateMessagesForQuestion(question.getId());
            if (privateMessages.isEmpty()) {
                content.getChildren().add(new Label("No private feedback"));
            } else {
                for (PrivateMessage pm : privateMessages) {
                    VBox pmBox = new VBox(5);
                    pmBox.setPadding(new Insets(10));
                    pmBox.setStyle("-fx-border-color: #9b59b6; -fx-border-width: 1; -fx-background-color: #f4ecf7;");
                    
                    Label pmMeta = new Label(String.format("From: %s | To: %s | Type: %s | Posted: %s",
                        pm.getSender(), pm.getTo(), pm.getMessageType(), pm.getCreatedAt().format(
                            java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))));
                    pmMeta.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
                    
                    Label pmContent = new Label(pm.getContent());
                    pmContent.setWrapText(true);
                    
                    pmBox.getChildren().addAll(pmMeta, pmContent);
                    content.getChildren().add(pmBox);
                }
            }
            
        } catch (SQLException e) {
            Label errorLabel = new Label("Error loading details: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            content.getChildren().add(errorLabel);
        }
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        
        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
    
    /**
     * Creates the Answers Review tab for reviewing all answers in the system.
     * 
     * @return Tab containing the answers review interface
     */
    private Tab createAnswersReviewTab() {
        Tab tab = new Tab("Answers Review");
        tab.setClosable(false);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("Answers Review");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Filter options
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label filterLabel = new Label("Sort by:");
        ComboBox<String> sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll("Most Recent", "Most Upvoted", "By Reviewers");
        sortCombo.setValue("Most Recent");
        
        Button applyButton = new Button("Apply");
        applyButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        
        filterBox.getChildren().addAll(filterLabel, sortCombo, applyButton);
        
        // Answers display
        VBox answersDisplay = new VBox(10);
        
        Runnable loadAnswers = () -> {
            answersDisplay.getChildren().clear();
            try {
                List<Question> allQuestions = databaseHelper.getAllQuestions(null);
                List<Answer> allAnswers = new ArrayList<>();
                
                for (Question q : allQuestions) {
                    List<Answer> answers = databaseHelper.getAnswersForQuestion(q.getId());
                    allAnswers.addAll(answers);
                }
                
                // Sort based on selection
                String sortBy = sortCombo.getValue();
                if ("Most Upvoted".equals(sortBy)) {
                    allAnswers.sort((a1, a2) -> Integer.compare(a2.getUpvotes(), a1.getUpvotes()));
                } else if ("By Reviewers".equals(sortBy)) {
                    allAnswers.removeIf(a -> {
                        try {
                            String role = databaseHelper.getUserRole(a.getAnsweredBy());
                            return !"Reviewer".equalsIgnoreCase(role);
                        } catch (Exception e) {
                            return true;
                        }
                    });
                }
                
                if (allAnswers.isEmpty()) {
                    answersDisplay.getChildren().add(new Label("No answers to display"));
                } else {
                    for (Answer answer : allAnswers) {
                        answersDisplay.getChildren().add(createAnswerReviewCard(answer));
                    }
                }
                
            } catch (SQLException e) {
                Label errorLabel = new Label("Error loading answers: " + e.getMessage());
                errorLabel.setStyle("-fx-text-fill: red;");
                answersDisplay.getChildren().add(errorLabel);
            }
        };
        
        applyButton.setOnAction(e -> loadAnswers.run());
        loadAnswers.run(); // Initial load
        
        content.getChildren().addAll(titleLabel, filterBox, new Separator(), answersDisplay);
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        tab.setContent(scrollPane);
        
        return tab;
    }
    
    /**
     * Creates a review card for a single answer.
     * 
     * @param answer The answer to create a card for
     * @return VBox containing the answer review card
     */
    private VBox createAnswerReviewCard(Answer answer) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-background-color: #fafafa;");
        
        // Header
        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label authorLabel = new Label("By: " + answer.getAnsweredBy());
        authorLabel.setStyle("-fx-font-weight: bold;");
        
        Label upvotesLabel = new Label("👍 " + answer.getUpvotes());
        upvotesLabel.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 3 8; -fx-font-size: 11px;");
        
        Label dateLabel = new Label(answer.getFormattedDate());
        dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
        
        header.getChildren().addAll(authorLabel, upvotesLabel, dateLabel);
        
        // Content
        Label contentLabel = new Label(answer.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-font-size: 12px;");
        
        // Question context
        try {
            Question question = databaseHelper.getQuestionById(answer.getQuestionId());
            if (question != null) {
                Label contextLabel = new Label("In response to: " + question.getTitle());
                contextLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
                card.getChildren().add(contextLabel);
            }
        } catch (SQLException e) {
            // Silently fail
        }
        
        // Action buttons
        HBox buttonBox = new HBox(10);
        
        Button viewQuestionButton = new Button("View Question");
        viewQuestionButton.setStyle("-fx-font-size: 11px;");
        viewQuestionButton.setOnAction(e -> {
            try {
                Question q = databaseHelper.getQuestionById(answer.getQuestionId());
                if (q != null) {
                    showQuestionDetails(q);
                }
            } catch (SQLException ex) {
                showAlert("Error", "Failed to load question details", AlertType.ERROR);
            }
        });
        
        Button postReviewButton = new Button("✍️ Post Review");
        postReviewButton.setStyle("-fx-font-size: 11px; -fx-background-color: #9b59b6; -fx-text-fill: white;");
        postReviewButton.setOnAction(e -> postReviewOnAnswer(answer));
        
        Button flagButton = new Button("🚩 Flag");
        flagButton.setStyle("-fx-font-size: 11px;");
        flagButton.setOnAction(e -> flagAnswer(answer));
        
        buttonBox.getChildren().addAll(viewQuestionButton, postReviewButton, flagButton);
        
        card.getChildren().addAll(header, contentLabel, buttonBox);
        
        return card;
    }
    
    /**
     * Creates the Reviews tab for viewing all reviews in the system.
     * 
     * @return Tab containing the reviews interface
     */
    private Tab createReviewsTab() {
        Tab tab = new Tab("Reviews");
        tab.setClosable(false);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("All Reviews");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label infoLabel = new Label("View and monitor all reviews posted by staff and reviewers");
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        // Reviews display
        VBox reviewsDisplay = new VBox(10);
        
        try {
            List<Review> allReviews = databaseHelper.getAllReviews(null);
            
            if (allReviews.isEmpty()) {
                reviewsDisplay.getChildren().add(new Label("No reviews posted yet"));
            } else {
                // Create header
                GridPane headerGrid = new GridPane();
                headerGrid.setHgap(10);
                headerGrid.setVgap(5);
                headerGrid.setPadding(new Insets(5, 0, 5, 0));
                headerGrid.setStyle("-fx-border-color: #ccc; -fx-border-width: 0 0 1 0;");
                headerGrid.getColumnConstraints().addAll(
                    new ColumnConstraints(300, 450, 600), // Review Text
                    new ColumnConstraints(120, 150, 200),  // Reviewer
                    new ColumnConstraints(100)             // Posted Date
                );
                
                Label reviewHeader = new Label("Review");
                reviewHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
                Label reviewerHeader = new Label("Reviewer");
                reviewerHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
                Label dateHeader = new Label("Posted");
                dateHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
                
                headerGrid.add(reviewHeader, 0, 0);
                headerGrid.add(reviewerHeader, 1, 0);
                headerGrid.add(dateHeader, 2, 0);
                
                reviewsDisplay.getChildren().add(headerGrid);
                
                for (Review review : allReviews) {
                    VBox reviewCard = new VBox(10);
                    reviewCard.setPadding(new Insets(15));
                    reviewCard.setStyle("-fx-border-color: #9b59b6; -fx-border-width: 2; -fx-background-color: #f4ecf7;");
                    
                    // Get the answer this review is for
                    try {
                        Question question = databaseHelper.getQuestionByAnswerId(review.getAnswerId());
                        if (question != null) {
                            Label contextLabel = new Label("Review for answer in: " + question.getTitle());
                            contextLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
                            reviewCard.getChildren().add(contextLabel);
                        }
                    } catch (SQLException e) {
                        // Silently fail
                    }
                    
                    Label reviewerLabel = new Label("By: " + review.getWrittenBy());
                    reviewerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
                    
                    Label contentLabel = new Label(review.getReviewText());
                    contentLabel.setWrapText(true);
                    contentLabel.setStyle("-fx-font-size: 12px;");
                    
                    Label dateLabel = new Label(review.getCreatedAt().format(
                        java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
                    dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
                    
                    Button viewAnswerButton = new Button("View Answer & Question");
                    viewAnswerButton.setStyle("-fx-font-size: 11px;");
                    viewAnswerButton.setOnAction(e -> {
                        try {
                            Question q = databaseHelper.getQuestionByAnswerId(review.getAnswerId());
                            if (q != null) {
                                showQuestionDetails(q);
                            }
                        } catch (SQLException ex) {
                            showAlert("Error", "Failed to load question: " + ex.getMessage(), AlertType.ERROR);
                        }
                    });
                    
                    reviewCard.getChildren().addAll(reviewerLabel, contentLabel, dateLabel, viewAnswerButton);
                    reviewsDisplay.getChildren().add(reviewCard);
                }
            }
            
        } catch (SQLException e) {
            Label errorLabel = new Label("Error loading reviews: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            reviewsDisplay.getChildren().add(errorLabel);
        }
        
        content.getChildren().addAll(titleLabel, infoLabel, new Separator(), reviewsDisplay);
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        tab.setContent(scrollPane);
        
        return tab;
    }
    
    /**
     * Creates the Feedback Review tab for reviewing private feedback messages.
     * 
     * @return Tab containing the feedback review interface
     */
    private Tab createFeedbackReviewTab() {
        Tab tab = new Tab("Feedback Review");
        tab.setClosable(false);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("Private Feedback Review");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label infoLabel = new Label("Monitor private feedback messages to identify potential issues or conflicts");
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        // Feedback display
        VBox feedbackDisplay = new VBox(10);
        
        try {
            List<Question> allQuestions = databaseHelper.getAllQuestions(null);
            int totalFeedback = 0;
            
            for (Question q : allQuestions) {
                List<PrivateMessage> messages = databaseHelper.getPrivateMessagesForQuestion(q.getId());
                
                for (PrivateMessage pm : messages) {
                    VBox messageCard = new VBox(10);
                    messageCard.setPadding(new Insets(15));
                    messageCard.setStyle("-fx-border-color: #9b59b6; -fx-border-width: 2; -fx-background-color: #f4ecf7;");
                    
                    Label headerLabel = new Label(String.format("%s → %s (%s)", 
                        pm.getSender(), pm.getTo(), pm.getMessageType()));
                    headerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
                    
                    Label questionLabel = new Label("Regarding: " + q.getTitle());
                    questionLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
                    
                    Label contentLabel = new Label(pm.getContent());
                    contentLabel.setWrapText(true);
                    contentLabel.setStyle("-fx-font-size: 12px;");
                    
                    Label dateLabel = new Label(pm.getCreatedAt().format(
                        java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
                    dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
                    
                    Button viewThreadButton = new Button("View Full Thread");
                    viewThreadButton.setStyle("-fx-font-size: 11px;");
                    viewThreadButton.setOnAction(e -> showQuestionDetails(q));
                    
                    messageCard.getChildren().addAll(headerLabel, questionLabel, contentLabel, dateLabel, viewThreadButton);
                    feedbackDisplay.getChildren().add(messageCard);
                    totalFeedback++;
                }
            }
            
            if (totalFeedback == 0) {
                feedbackDisplay.getChildren().add(new Label("No private feedback to review"));
            }
            
        } catch (SQLException e) {
            Label errorLabel = new Label("Error loading feedback: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            feedbackDisplay.getChildren().add(errorLabel);
        }
        
        content.getChildren().addAll(titleLabel, infoLabel, new Separator(), feedbackDisplay);
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        tab.setContent(scrollPane);
        
        return tab;
    }
    
    /**
     * Creates the Alerts tab for viewing flagged content and potential issues.
     * 
     * @return Tab containing the alerts interface
     */
    private Tab createAlertsTab() {
        Tab tab = new Tab("Alerts & Flags");
        tab.setClosable(false);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("System Alerts & Flagged Content");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label infoLabel = new Label("Flagged items and system-generated alerts appear here");
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        // Alerts display
        VBox alertsDisplay = new VBox(10);
        
        try {
            // Show staff flags
            List<DatabaseHelper.StaffFlag> flags = databaseHelper.getOpenStaffFlags();
            
            if (!flags.isEmpty()) {
                Label flagsHeader = new Label("🚩 Flagged Content (" + flags.size() + "):");
                flagsHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 0;");
                alertsDisplay.getChildren().add(flagsHeader);
                
                for (DatabaseHelper.StaffFlag flag : flags) {
                    VBox flagCard = new VBox(10);
                    flagCard.setPadding(new Insets(15));
                    flagCard.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2; -fx-background-color: #fadbd8;");
                    
                    Label typeLabel = new Label(flag.getItemType() + " #" + flag.getItemId());
                    typeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    
                    Label reasonLabel = new Label("Reason: " + flag.getReason());
                    reasonLabel.setWrapText(true);
                    reasonLabel.setStyle("-fx-font-size: 12px;");
                    
                    Label metaLabel = new Label("Flagged by: " + flag.getFlaggedBy() + " | " + 
                        flag.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
                    metaLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
                    
                    Button viewButton = new Button("View Item");
                    viewButton.setStyle("-fx-font-size: 11px;");
                    viewButton.setOnAction(e -> {
                        try {
                            if ("QUESTION".equals(flag.getItemType())) {
                                Question q = databaseHelper.getQuestionById(flag.getItemId());
                                if (q != null) showQuestionDetails(q);
                            } else if ("ANSWER".equals(flag.getItemType())) {
                                // Find the answer and show its question
                                List<Question> allQuestions = databaseHelper.getAllQuestions(null);
                                for (Question q : allQuestions) {
                                    for (Answer a : databaseHelper.getAnswersForQuestion(q.getId())) {
                                        if (a.getId() == flag.getItemId()) {
                                            showQuestionDetails(q);
                                            return;
                                        }
                                    }
                                }
                            }
                        } catch (SQLException ex) {
                            showAlert("Error", "Failed to load item: " + ex.getMessage(), AlertType.ERROR);
                        }
                    });
                    
                    flagCard.getChildren().addAll(typeLabel, reasonLabel, metaLabel, viewButton);
                    alertsDisplay.getChildren().add(flagCard);
                }
            }
            
            // Check for potential issues
            List<Question> unresolvedQuestions = databaseHelper.getUnresolvedQuestions();
            
            // Alert for questions with no answers for extended period
            for (Question q : unresolvedQuestions) {
                if (q.getAnswers().isEmpty()) {
                    VBox alertCard = createAlertCard(
                        "Unanswered Question",
                        "Question \"" + q.getTitle() + "\" by " + q.getAskedBy() + " has received no answers",
                        "warning",
                        () -> showQuestionDetails(q)
                    );
                    alertsDisplay.getChildren().add(alertCard);
                }
            }
            
            // Alert for questions with many answers but still unresolved
            for (Question q : unresolvedQuestions) {
                if (q.getAnswers().size() >= 5) {
                    VBox alertCard = createAlertCard(
                        "High Activity, Unresolved",
                        "Question \"" + q.getTitle() + "\" has " + q.getAnswers().size() + " answers but remains unresolved",
                        "info",
                        () -> showQuestionDetails(q)
                    );
                    alertsDisplay.getChildren().add(alertCard);
                }
            }
            
            if (alertsDisplay.getChildren().isEmpty()) {
                alertsDisplay.getChildren().add(new Label("No alerts at this time"));
            }
            
        } catch (SQLException e) {
            Label errorLabel = new Label("Error loading alerts: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            alertsDisplay.getChildren().add(errorLabel);
        }
        
        content.getChildren().addAll(titleLabel, infoLabel, new Separator(), alertsDisplay);
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        tab.setContent(scrollPane);
        
        return tab;
    }
    
    /**
     * Creates an alert card with the specified parameters.
     * 
     * @param title The alert title
     * @param message The alert message
     * @param type The alert type (warning, error, info)
     * @param action The action to perform when clicked
     * @return VBox containing the alert card
     */
    private VBox createAlertCard(String title, String message, String type, Runnable action) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        
        String color = switch (type) {
            case "error" -> "#e74c3c";
            case "warning" -> "#f39c12";
            case "info" -> "#3498db";
            default -> "#95a5a6";
        };
        
        card.setStyle(String.format("-fx-border-color: %s; -fx-border-width: 2; -fx-background-color: %s20;", color, color));
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 12px;");
        
        Button actionButton = new Button("Review");
        actionButton.setStyle("-fx-font-size: 11px;");
        actionButton.setOnAction(e -> action.run());
        
        card.getChildren().addAll(titleLabel, messageLabel, actionButton);
        
        return card;
    }
    
    /**
     * Flags a question for review by instructors.
     * 
     * @param question The question to flag
     */
    private void flagQuestion(Question question) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Flag Question");
        dialog.setHeaderText("Flag question: " + question.getTitle());
        dialog.setContentText("Reason for flagging:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(reason -> {
            if (!reason.trim().isEmpty()) {
                try {
                    databaseHelper.addStaffFlag("QUESTION", question.getId(), 
                        currentUser.getUserName(), reason);
                    showAlert("Success", "Question flagged for instructor review", AlertType.INFORMATION);
                    refreshAllTabs();
                } catch (SQLException e) {
                    showAlert("Error", "Failed to flag question: " + e.getMessage(), AlertType.ERROR);
                }
            }
        });
    }
    
    /**
     * Flags an answer for review by instructors.
     * 
     * @param answer The answer to flag
     */
    private void flagAnswer(Answer answer) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Flag Answer");
        dialog.setHeaderText("Flag answer by: " + answer.getAnsweredBy());
        dialog.setContentText("Reason for flagging:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(reason -> {
            if (!reason.trim().isEmpty()) {
                try {
                    databaseHelper.addStaffFlag("ANSWER", answer.getId(), 
                        currentUser.getUserName(), reason);
                    showAlert("Success", "Answer flagged for instructor review", AlertType.INFORMATION);
                    refreshAllTabs();
                } catch (SQLException e) {
                    showAlert("Error", "Failed to flag answer: " + e.getMessage(), AlertType.ERROR);
                }
            }
        });
    }
    
    /**
     * Adds a staff note to a question.
     * 
     * @param question The question to add a note to
     */
    private void addStaffNote(Question question) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Staff Note");
        dialog.setHeaderText("Add note for: " + question.getTitle());
        dialog.setContentText("Note:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(note -> {
            if (!note.trim().isEmpty()) {
                try {
                    databaseHelper.addStaffNote(question.getId(), note, currentUser.getUserName());
                    showAlert("Success", "Staff note added", AlertType.INFORMATION);
                    refreshAllTabs();
                } catch (SQLException e) {
                    showAlert("Error", "Failed to add note: " + e.getMessage(), AlertType.ERROR);
                }
            }
        });
    }
    
    /**
     * Refreshes all tabs to show the latest data.
     */
    private void refreshAllTabs() {
        int selectedIndex = tabPane.getSelectionModel().getSelectedIndex();
        
        tabPane.getTabs().clear();
        
        Tab overviewTab = createOverviewTab();
        Tab questionsTab = createQuestionsReviewTab();
        Tab answersTab = createAnswersReviewTab();
        Tab reviewsTab = createReviewsTab();
        Tab feedbackTab = createFeedbackReviewTab();
        Tab alertsTab = createAlertsTab();
        
        tabPane.getTabs().addAll(overviewTab, questionsTab, answersTab, reviewsTab, feedbackTab, alertsTab);
        
        if (selectedIndex >= 0 && selectedIndex < tabPane.getTabs().size()) {
            tabPane.getSelectionModel().select(selectedIndex);
        }
    }
    
    /**
     * Shows an alert dialog with the specified parameters.
     * 
     * @param title The alert title
     * @param content The alert content
     * @param type The alert type
     */
    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
