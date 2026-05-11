package moviecriticssystem;

import java.sql.*;
import javax.swing.JOptionPane;

public class ChildFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ChildFrame.class.getName());
    private int userId;
    private int selectedMovieId = -1;
    private javax.swing.JPanel selectedCardPanel;

    public ChildFrame(int userId) {
        this.userId = userId;
        initComponents();
        Theme.applyToFrame(this);
        loadGenres();
        loadMovieCards();
    }

    private void loadGenres() {
        GenreFilterCombo.removeAllItems();
        GenreFilterCombo.addItem("All");
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT DISTINCT Genre FROM Movies WHERE ParentalRestriction = FALSE ORDER BY Genre")) {
            while (rs.next()) GenreFilterCombo.addItem(rs.getString("Genre"));
        } catch (Exception e) { /* ignore */ }
    }

    private void loadMovieCards() {
        Movies.removeAll();
        selectedMovieId = -1;
        selectedCardPanel = null;

        String genre   = (String) GenreFilterCombo.getSelectedItem();
        String keyword = SearchTextField.getText().trim();

        String sql = "SELECT m.MovieID, m.Title, m.Poster, m.Rating, m.Genre, m.ParentalRestriction, " +
                     "CONCAT(p.FirstName,' ',p.LastName) AS LeadActor " +
                     "FROM Movies m LEFT JOIN Persons p ON m.LeadingActorId = p.PersonID " +
                     "WHERE m.ParentalRestriction = FALSE";
        if (keyword != null && !keyword.isEmpty())
            sql += " AND (m.Title LIKE '%" + keyword + "%' OR m.Language LIKE '%" + keyword + "%'" +
                   " OR YEAR(m.ReleaseDate) LIKE '%" + keyword + "%')";
        if (genre != null && !genre.equals("All"))
            sql += " AND m.Genre = '" + genre + "'";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {
            while (rs.next()) {
                moviecriticssystem.Movie card = new moviecriticssystem.Movie(
                    rs.getInt("MovieID"), rs.getString("Title"),
                    rs.getString("Poster"), rs.getInt("Rating"),
                    rs.getString("LeadActor"), rs.getBoolean("ParentalRestriction"),
                    rs.getString("Genre"));
                card.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                        if (selectedCardPanel != null) Theme.deselectCard(selectedCardPanel);
                        selectedMovieId   = card.getMovieId();
                        selectedCardPanel = card;
                        Theme.selectCard(card);
                        if (e.getClickCount() == 2) showMovieDetail(card.getMovieId());
                    }
                });
                Movies.add(card);
            }
        } catch (Exception e) {
            Movies.add(new javax.swing.JLabel("Error: " + e.getMessage()));
        }
        Movies.revalidate();
        Movies.repaint();
    }

    private void showMovieDetail(int movieId) {
        String sql =
            "SELECT m.*, YEAR(m.ReleaseDate) AS ReleaseYear, " +
            "CONCAT(d.FirstName,' ',d.LastName) AS Director, " +
            "CONCAT(la.FirstName,' ',la.LastName) AS LeadActor, " +
            "CONCAT(sa.FirstName,' ',sa.LastName) AS SupportActor " +
            "FROM Movies m " +
            "LEFT JOIN Persons d  ON m.DirectorId        = d.PersonID " +
            "LEFT JOIN Persons la ON m.LeadingActorId    = la.PersonID " +
            "LEFT JOIN Persons sa ON m.SupportingActorId = sa.PersonID " +
            "WHERE m.MovieID = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, movieId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String info = String.format(
                    "Title    : %s%nYear     : %s%nGenre    : %s%n" +
                    "Language : %s%nCountry  : %s%nDirector : %s%n" +
                    "Lead     : %s%nSupport  : %s%n" +
                    "Rating   : %d/10%nWatched  : %s%n%nComments:%n%s",
                    rs.getString("Title"), rs.getString("ReleaseYear"),
                    rs.getString("Genre"), rs.getString("Language"),
                    rs.getString("CountryOfOrigin"), rs.getString("Director"),
                    rs.getString("LeadActor"), rs.getString("SupportActor"),
                    rs.getInt("Rating"),
                    rs.getBoolean("Watched") ? "Yes" : "No",
                    rs.getString("Comments") != null ? rs.getString("Comments") : "-");
                JOptionPane.showMessageDialog(this, info,
                    rs.getString("Title"), JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void upsertInteraction(int movieId, String field, Object value) {
        try (Connection conn = DatabaseConnection.connect()) {
            PreparedStatement check = conn.prepareStatement(
                "SELECT InteractionID FROM UserMovieInteractions WHERE UserID=? AND MovieID=?");
            check.setInt(1, userId);
            check.setInt(2, movieId);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                PreparedStatement upd = conn.prepareStatement(
                    "UPDATE UserMovieInteractions SET " + field + "=?, Status='pending' WHERE UserID=? AND MovieID=?");
                if (value instanceof Integer) upd.setInt(1, (Integer) value);
                else if (value instanceof Boolean) upd.setBoolean(1, (Boolean) value);
                else upd.setString(1, (String) value);
                upd.setInt(2, userId);
                upd.setInt(3, movieId);
                upd.executeUpdate();
            } else {
                PreparedStatement ins = conn.prepareStatement(
                    "INSERT INTO UserMovieInteractions (UserID, MovieID, " + field + ", Status) VALUES (?,?,?,'pending')");
                ins.setInt(1, userId);
                ins.setInt(2, movieId);
                if (value instanceof Integer) ins.setInt(3, (Integer) value);
                else if (value instanceof Boolean) ins.setBoolean(3, (Boolean) value);
                else ins.setString(3, (String) value);
                ins.executeUpdate();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Buttons = new javax.swing.JPanel();
        MarkWatchedButton = new javax.swing.JButton();
        RateMovieButton = new javax.swing.JButton();
        AddCommentButton = new javax.swing.JButton();
        WatchlistButton = new javax.swing.JButton();
        ProgressButton = new javax.swing.JButton();
        FamilyRatingsButton = new javax.swing.JButton();
        AddWatchlistButton = new javax.swing.JButton();
        RemoveWatchlistButton = new javax.swing.JButton();
        Search = new javax.swing.JPanel();
        SearchTextField = new javax.swing.JTextField();
        GenreFilterCombo = new javax.swing.JComboBox<>();
        SearchButton1 = new javax.swing.JButton();
        ClearButton1 = new javax.swing.JButton();
        RefreshButton = new javax.swing.JButton();
        movieScrollPane = new javax.swing.JScrollPane();
        Movies = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(400, 500));

        Buttons.setPreferredSize(new java.awt.Dimension(200, 400));
        Buttons.setLayout(new java.awt.GridLayout(0, 1));

        MarkWatchedButton.setText("Mark Watched");
        MarkWatchedButton.addActionListener(this::MarkWatchedButtonActionPerformed);
        Buttons.add(MarkWatchedButton);

        RateMovieButton.setText("Rate Movie");
        RateMovieButton.addActionListener(this::RateMovieButtonActionPerformed);
        Buttons.add(RateMovieButton);

        AddCommentButton.setText("Add Comment");
        AddCommentButton.addActionListener(this::AddCommentButtonActionPerformed);
        Buttons.add(AddCommentButton);

        WatchlistButton.setText("My Watchlist");
        WatchlistButton.addActionListener(this::WatchlistButtonActionPerformed);
        Buttons.add(WatchlistButton);

        ProgressButton.setText("My Progress");
        ProgressButton.addActionListener(this::ProgressButtonActionPerformed);
        Buttons.add(ProgressButton);

        FamilyRatingsButton.setText("Family Ratings");
        FamilyRatingsButton.addActionListener(this::FamilyRatingsButtonActionPerformed);
        Buttons.add(FamilyRatingsButton);

        AddWatchlistButton.setText("Add  Watchlist");
        AddWatchlistButton.addActionListener(this::AddWatchlistButtonActionPerformed);
        Buttons.add(AddWatchlistButton);

        RemoveWatchlistButton.setText("Remove Watchlist");
        RemoveWatchlistButton.addActionListener(this::RemoveWatchlistButtonActionPerformed);
        Buttons.add(RemoveWatchlistButton);

        getContentPane().add(Buttons, java.awt.BorderLayout.WEST);

        Search.setPreferredSize(new java.awt.Dimension(1426, 50));

        SearchButton1.setText("Search");
        SearchButton1.addActionListener(this::SearchButton1ActionPerformed);

        ClearButton1.setText("Clear");
        ClearButton1.addActionListener(this::ClearButton1ActionPerformed);

        RefreshButton.setText("Refresh");
        RefreshButton.addActionListener(this::RefreshButtonActionPerformed);

        javax.swing.GroupLayout SearchLayout = new javax.swing.GroupLayout(Search);
        Search.setLayout(SearchLayout);
        SearchLayout.setHorizontalGroup(
            SearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(GenreFilterCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SearchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(SearchButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ClearButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RefreshButton)
                .addContainerGap())
        );
        SearchLayout.setVerticalGroup(
            SearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SearchLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(SearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(GenreFilterCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SearchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SearchButton1)
                    .addComponent(ClearButton1)
                    .addComponent(RefreshButton))
                .addGap(14, 14, 14))
        );

        getContentPane().add(Search, java.awt.BorderLayout.PAGE_START);

        movieScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        Movies.setMinimumSize(new java.awt.Dimension(400, 500));
        Movies.setLayout(new java.awt.GridLayout(0, 3));
        movieScrollPane.setViewportView(Movies);

        getContentPane().add(movieScrollPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void MarkWatchedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MarkWatchedButtonActionPerformed
        if (selectedMovieId == -1) { JOptionPane.showMessageDialog(this, "Select a movie first!"); return; }
        upsertInteraction(selectedMovieId, "Watched", true);
        JOptionPane.showMessageDialog(this, "Marked as watched!");
        loadMovieCards();
    }//GEN-LAST:event_MarkWatchedButtonActionPerformed

    private void RateMovieButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RateMovieButtonActionPerformed
        if (selectedMovieId == -1) { JOptionPane.showMessageDialog(this, "Select a movie first!"); return; }
        String input = JOptionPane.showInputDialog(this, "Enter rating (1-10):");
        if (input == null || input.trim().isEmpty()) return;
        int score;
        try {
            score = Integer.parseInt(input.trim());
            if (score < 1 || score > 10) { JOptionPane.showMessageDialog(this, "Rating must be between 1-10!"); return; }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid number!"); return;
        }
        upsertInteraction(selectedMovieId, "Rating", score);
        JOptionPane.showMessageDialog(this, "Rating pending approval");
    }//GEN-LAST:event_RateMovieButtonActionPerformed

    private void AddCommentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddCommentButtonActionPerformed
        if (selectedMovieId == -1) { JOptionPane.showMessageDialog(this, "Select a movie first!"); return; }
        String comment = JOptionPane.showInputDialog(this, "Enter your comment:");
        if (comment == null || comment.trim().isEmpty()) return;
        upsertInteraction(selectedMovieId, "Comment", comment.trim());
        JOptionPane.showMessageDialog(this, "Comment pending approval");
    }//GEN-LAST:event_AddCommentButtonActionPerformed

    private void WatchlistButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WatchlistButtonActionPerformed
        try (Connection conn = DatabaseConnection.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT m.Title, m.Genre, m.Rating " +
                "FROM Movies m " +
                "JOIN UserMovieInteractions i ON m.MovieID = i.MovieID " +
                "WHERE i.UserID = ? AND i.Watchlist = TRUE " +
                "ORDER BY m.Title");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            StringBuilder sb = new StringBuilder("My Watchlist:\n\n");
            boolean hasMovies = false;
            while (rs.next()) {
                sb.append("• ").append(rs.getString("Title"))
                  .append(" (").append(rs.getString("Genre")).append(")")
                  .append(" - Rating: ").append(rs.getInt("Rating")).append("/10\n");
                hasMovies = true;
            }
            if (!hasMovies) sb.append("Your watchlist is empty!");
            JOptionPane.showMessageDialog(this, sb.toString(), "My Watchlist", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_WatchlistButtonActionPerformed

    private void ProgressButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ProgressButtonActionPerformed
        try (Connection conn = DatabaseConnection.connect()) {
            PreparedStatement ps1 = conn.prepareStatement(
                "SELECT COUNT(*) as total FROM Movies WHERE ParentalRestriction = FALSE");
            ResultSet rs1 = ps1.executeQuery();
            int total = rs1.next() ? rs1.getInt("total") : 0;

            PreparedStatement ps2 = conn.prepareStatement(
                "SELECT COUNT(*) as watched FROM UserMovieInteractions WHERE UserID = ? AND Watched = TRUE");
            ps2.setInt(1, userId);
            ResultSet rs2 = ps2.executeQuery();
            int myWatched = rs2.next() ? rs2.getInt("watched") : 0;

            PreparedStatement ps3 = conn.prepareStatement(
                "SELECT u.Username, COUNT(i.InteractionID) as watched " +
                "FROM Users u " +
                "LEFT JOIN UserMovieInteractions i ON u.UserId = i.UserID AND i.Watched = TRUE " +
                "WHERE u.UserType = 2 " +
                "GROUP BY u.UserId, u.Username " +
                "ORDER BY watched DESC");
            ResultSet rs3 = ps3.executeQuery();

            StringBuilder sb = new StringBuilder();
            sb.append("Total Available Movies : ").append(total).append("\n");
            sb.append("You Watched            : ").append(myWatched).append("\n");
            sb.append("Remaining              : ").append(total - myWatched).append("\n\n");
            sb.append("--- Family Progress ---\n");
            while (rs3.next()) {
                sb.append(rs3.getString("Username")).append(" : ")
                  .append(rs3.getInt("watched")).append(" watched\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "My Progress", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_ProgressButtonActionPerformed

    private void FamilyRatingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FamilyRatingsButtonActionPerformed
        if (selectedMovieId == -1) { JOptionPane.showMessageDialog(this, "Select a movie first!"); return; }
        try (Connection conn = DatabaseConnection.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT u.Username, i.Rating, i.Comment " +
                "FROM UserMovieInteractions i " +
                "JOIN Users u ON i.UserID = u.UserId " +
                "WHERE i.MovieID = ? AND i.Status = 'approved'");
            ps.setInt(1, selectedMovieId);
            ResultSet rs = ps.executeQuery();
            StringBuilder sb = new StringBuilder("Family Ratings\n\n");
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                sb.append(rs.getString("Username")).append("\n");
                sb.append("Rating: ").append(rs.getObject("Rating") != null ? rs.getInt("Rating") + "/10" : "-").append("\n");
                sb.append("Comment: ").append(rs.getString("Comment") != null ? rs.getString("Comment") : "-").append("\n\n");
            }
            if (!hasData) sb.append("No ratings or comments yet.");
            JOptionPane.showMessageDialog(this, sb.toString(), "Family Ratings", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_FamilyRatingsButtonActionPerformed

    private void AddWatchlistButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddWatchlistButtonActionPerformed
        if (selectedMovieId == -1) { JOptionPane.showMessageDialog(this, "Select a movie first!"); return; }
        upsertInteraction(selectedMovieId, "Watchlist", true);
        JOptionPane.showMessageDialog(this, "Added to watchlist!");
    }//GEN-LAST:event_AddWatchlistButtonActionPerformed

    private void RemoveWatchlistButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveWatchlistButtonActionPerformed
        if (selectedMovieId == -1) { JOptionPane.showMessageDialog(this, "Select a movie first!"); return; }
        upsertInteraction(selectedMovieId, "Watchlist", false);
        JOptionPane.showMessageDialog(this, "Removed from watchlist!");
    }//GEN-LAST:event_RemoveWatchlistButtonActionPerformed

    private void SearchButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchButton1ActionPerformed
        loadMovieCards();
    }//GEN-LAST:event_SearchButton1ActionPerformed

    private void ClearButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearButton1ActionPerformed
        SearchTextField.setText("");
        GenreFilterCombo.setSelectedIndex(0);
        loadMovieCards();
    }//GEN-LAST:event_ClearButton1ActionPerformed

    private void RefreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshButtonActionPerformed
        loadGenres();
        loadMovieCards();
    }//GEN-LAST:event_RefreshButtonActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new ChildFrame(1).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddCommentButton;
    private javax.swing.JButton AddWatchlistButton;
    private javax.swing.JPanel Buttons;
    private javax.swing.JButton ClearButton1;
    private javax.swing.JButton FamilyRatingsButton;
    private javax.swing.JComboBox<String> GenreFilterCombo;
    private javax.swing.JButton MarkWatchedButton;
    private javax.swing.JPanel Movies;
    private javax.swing.JButton ProgressButton;
    private javax.swing.JButton RateMovieButton;
    private javax.swing.JButton RefreshButton;
    private javax.swing.JButton RemoveWatchlistButton;
    private javax.swing.JPanel Search;
    private javax.swing.JButton SearchButton1;
    private javax.swing.JTextField SearchTextField;
    private javax.swing.JButton WatchlistButton;
    private javax.swing.JScrollPane movieScrollPane;
    // End of variables declaration//GEN-END:variables
}
