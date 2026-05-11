package moviecriticssystem;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import javax.swing.JOptionPane;
import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;

public class AdultFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AdultFrame.class.getName());

    private int userId;
    private int selectedMovieId = -1;
    private javax.swing.JPanel selectedCardPanel;

    /**
     * Creates new form MainFrame
     */
    public AdultFrame(int userId) {
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
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT Genre FROM Movies ORDER BY Genre")) {
            while (rs.next()) GenreFilterCombo.addItem(rs.getString("Genre"));
        } catch (Exception e) { /* ignore */ }
    }

    private void loadMovieCards() {
        Movie.removeAll();
        selectedMovieId = -1;
        selectedCardPanel = null;

        String genre   = (String) GenreFilterCombo.getSelectedItem();
        String keyword = SearchTextField.getText().trim();

        String sql = "SELECT m.MovieID, m.Title, m.Poster, m.Rating, m.Genre, m.ParentalRestriction, " +
                     "CONCAT(p.FirstName,' ',p.LastName) AS LeadActor " +
                     "FROM Movies m LEFT JOIN Persons p ON m.LeadingActorId = p.PersonID WHERE 1=1";
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
                Movie.add(card);
            }
        } catch (Exception e) {
            Movie.add(new javax.swing.JLabel("Error: " + e.getMessage()));
        }
        Movie.revalidate();
        Movie.repaint();
    }

    private void showMovieDetail(int movieId) {
    String sql = "SELECT m.*, YEAR(m.ReleaseDate) AS ReleaseYear, " +
                 "CONCAT(d.FirstName,' ',d.LastName) AS Director, " +
                 "CONCAT(la.FirstName,' ',la.LastName) AS LeadActor, " +
                 "CONCAT(sa.FirstName,' ',sa.LastName) AS SupportActor " +
                 "FROM Movies m " +
                 "LEFT JOIN Persons d ON m.DirectorId = d.PersonID " +
                 "LEFT JOIN Persons la ON m.LeadingActorId = la.PersonID " +
                 "LEFT JOIN Persons sa ON m.SupportingActorId = sa.PersonID " +
                 "WHERE m.MovieID = ?";

    try (Connection conn = DatabaseConnection.connect();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, movieId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            String info = String.format(
                "Title : %s%nYear : %s%nGenre : %s%n" +
                "Language : %s%nCountry : %s%nDirector : %s%n" +
                "Lead : %s%nSupport : %s%n" +
                "Rating : %d/10%nWatched : %s%n%nAbout:%n%s%n%nComments:%n%s",
                rs.getString("Title"),
                rs.getString("ReleaseYear"),
                rs.getString("Genre"),
                rs.getString("Language"),
                rs.getString("CountryOfOrigin"),
                rs.getString("Director"),
                rs.getString("LeadActor"),
                rs.getString("SupportActor"),
                rs.getInt("Rating"),
                rs.getBoolean("Watched") ? "Yes" : "No",
                rs.getString("About") != null ? rs.getString("About") : "-",
                rs.getString("Comments") != null ? rs.getString("Comments") : "-");

            JOptionPane.showMessageDialog(this, info, rs.getString("Title"), JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Buttons = new javax.swing.JPanel();
        AddMovieButton = new javax.swing.JButton();
        DeleteMovieButton = new javax.swing.JButton();
        EdditMovieButton = new javax.swing.JButton();
        ManageUsersButton = new javax.swing.JButton();
        SetRestrictionButton = new javax.swing.JButton();
        AnalyticsButton = new javax.swing.JButton();
        ModerateContentButton = new javax.swing.JButton();
        ViewFamilyRatingsButton = new javax.swing.JButton();
        RefreshButton = new javax.swing.JButton();
        Search = new javax.swing.JPanel();
        GenreFilterCombo = new javax.swing.JComboBox<>();
        SearchTextField = new javax.swing.JTextField();
        SearchButton = new javax.swing.JButton();
        ClearButton = new javax.swing.JButton();
        MovieScrollPane = new javax.swing.JScrollPane();
        Movie = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Buttons.setPreferredSize(new java.awt.Dimension(200, 21));
        Buttons.setLayout(new java.awt.GridLayout(9, 1));

        AddMovieButton.setText("Add Movie");
        AddMovieButton.addActionListener(this::AddMovieButtonActionPerformed);
        Buttons.add(AddMovieButton);

        DeleteMovieButton.setText("Delete Movie");
        DeleteMovieButton.addActionListener(this::DeleteMovieButtonActionPerformed);
        Buttons.add(DeleteMovieButton);

        EdditMovieButton.setText("Edit Movie");
        EdditMovieButton.addActionListener(this::EdditMovieButtonActionPerformed);
        Buttons.add(EdditMovieButton);

        ManageUsersButton.setText("Manage Users");
        ManageUsersButton.addActionListener(this::ManageUsersButtonActionPerformed);
        Buttons.add(ManageUsersButton);

        SetRestrictionButton.setText("Set Restriction");
        SetRestrictionButton.addActionListener(this::SetRestrictionButtonActionPerformed);
        Buttons.add(SetRestrictionButton);

        AnalyticsButton.setText("Analytics");
        AnalyticsButton.addActionListener(this::AnalyticsButtonActionPerformed);
        Buttons.add(AnalyticsButton);

        ModerateContentButton.setText("Moderate Content");
        ModerateContentButton.addActionListener(this::ModerateContentButtonActionPerformed);
        Buttons.add(ModerateContentButton);

        ViewFamilyRatingsButton.setText("View Family Ratings");
        ViewFamilyRatingsButton.addActionListener(this::ViewFamilyRatingsButtonActionPerformed);
        Buttons.add(ViewFamilyRatingsButton);

        RefreshButton.setText("Refresh");
        RefreshButton.addActionListener(this::RefreshButtonActionPerformed);
        Buttons.add(RefreshButton);

        getContentPane().add(Buttons, java.awt.BorderLayout.WEST);
        Buttons.getAccessibleContext().setAccessibleName("Buttons");
        Buttons.getAccessibleContext().setAccessibleDescription("");

        Search.setPreferredSize(new java.awt.Dimension(1697, 50));

        GenreFilterCombo.addActionListener(this::GenreFilterComboActionPerformed);

        SearchButton.setText("Search");
        SearchButton.addActionListener(this::SearchButtonActionPerformed);

        ClearButton.setText("Clear");
        ClearButton.addActionListener(this::ClearButtonActionPerformed);

        javax.swing.GroupLayout SearchLayout = new javax.swing.GroupLayout(Search);
        Search.setLayout(SearchLayout);
        SearchLayout.setHorizontalGroup(
            SearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(GenreFilterCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(SearchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(SearchButton)
                .addGap(18, 18, 18)
                .addComponent(ClearButton)
                .addContainerGap(1643, Short.MAX_VALUE))
        );
        SearchLayout.setVerticalGroup(
            SearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SearchLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(SearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(GenreFilterCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SearchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SearchButton)
                    .addComponent(ClearButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(Search, java.awt.BorderLayout.PAGE_START);

        MovieScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        MovieScrollPane.setViewportBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        Movie.setLayout(new java.awt.GridLayout(0, 3));
        MovieScrollPane.setViewportView(Movie);

        getContentPane().add(MovieScrollPane, java.awt.BorderLayout.CENTER);
        MovieScrollPane.getAccessibleContext().setAccessibleName("movieScrollPane");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void RefreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshButtonActionPerformed
        loadGenres();
        loadMovieCards();
    }//GEN-LAST:event_RefreshButtonActionPerformed

    private void AddMovieButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddMovieButtonActionPerformed
        this.setEnabled(false); 
     AddMovieForm form = new AddMovieForm(this);  // sadece bu satır kalmalı
     form.setVisible(true);
    }//GEN-LAST:event_AddMovieButtonActionPerformed

    private void DeleteMovieButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteMovieButtonActionPerformed
        if (selectedMovieId == -1) { JOptionPane.showMessageDialog(this, "Select a movie first!"); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this movie?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try (Connection conn = DatabaseConnection.connect()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Movies WHERE MovieID = ?");
            ps.setInt(1, selectedMovieId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Deleted!");
            selectedMovieId = -1;
            loadMovieCards();
        } catch(Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }//GEN-LAST:event_DeleteMovieButtonActionPerformed

    private void EdditMovieButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EdditMovieButtonActionPerformed
        if (selectedMovieId == -1) { JOptionPane.showMessageDialog(this, "Select a movie first!"); return; }
        this.setEnabled(false);
        new EditMovieForm(selectedMovieId, this).setVisible(true);
    }//GEN-LAST:event_EdditMovieButtonActionPerformed

    private void SetRestrictionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SetRestrictionButtonActionPerformed
        if (selectedMovieId == -1) { JOptionPane.showMessageDialog(this, "Select a movie first!"); return; }
        try (Connection conn = DatabaseConnection.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE Movies SET ParentalRestriction = NOT ParentalRestriction WHERE MovieID = ?");
            ps.setInt(1, selectedMovieId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Restriction changed!");
            loadMovieCards();
        } catch(Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }//GEN-LAST:event_SetRestrictionButtonActionPerformed

    private void ManageUsersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ManageUsersButtonActionPerformed
        this.setEnabled(false);
     ManageUsersFrame form = new ManageUsersFrame(this);
     form.setVisible(true);
    }//GEN-LAST:event_ManageUsersButtonActionPerformed

    private void ModerateContentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModerateContentButtonActionPerformed
    this.setEnabled(false);
    ModerationFrame form = new ModerationFrame(this);
    form.setVisible(true);
    }//GEN-LAST:event_ModerateContentButtonActionPerformed

    private void AnalyticsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnalyticsButtonActionPerformed
       try (Connection conn = DatabaseConnection.connect()) {

        Statement stmt = conn.createStatement();


        ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) as total FROM Movies");
        int total = 0;
        if (rs1.next()) total = rs1.getInt("total");


        ResultSet rs2 = stmt.executeQuery("SELECT COUNT(DISTINCT MovieID) as watched FROM UserMovieInteractions WHERE Watched = TRUE");
        int watched = 0;
        if (rs2.next()) watched = rs2.getInt("watched");


        ResultSet rs3 = stmt.executeQuery("SELECT AVG(Rating) as avg FROM UserMovieInteractions WHERE Rating IS NOT NULL");
        double avg = 0;
        if (rs3.next()) avg = rs3.getDouble("avg");


        ResultSet rs4 = stmt.executeQuery(
        "SELECT m.Title, AVG(i.Rating) as Rating " +
        "FROM UserMovieInteractions i " +
        "JOIN Movies m ON i.MovieID = m.MovieID " +
        "WHERE i.Rating IS NOT NULL " +
        "GROUP BY m.MovieID, m.Title " +
        "ORDER BY AVG(i.Rating) DESC LIMIT 1");
        String topTitle = "-";
        int topRating = 0;
        if (rs4.next()) {
            topTitle = rs4.getString("Title");
            topRating = rs4.getInt("Rating");
        }


        String message =  "Total Movies     : " + total + "\n" + "Watched Movies   : " + watched + "\n" + "Unwatched Movies : " + (total - watched) + "\n" + "Average Rating   : " + String.format("%.1f", avg) + "\n" + "Top Rated Movie  : " + topTitle + " (" + topRating + "/10)";
         JOptionPane.showMessageDialog(this, message, "Analytics", JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
    }//GEN-LAST:event_AnalyticsButtonActionPerformed

    private void SearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchButtonActionPerformed
        loadMovieCards();
    }//GEN-LAST:event_SearchButtonActionPerformed

    private void ClearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearButtonActionPerformed
        SearchTextField.setText("");
        GenreFilterCombo.setSelectedIndex(0);
        loadMovieCards();
    }//GEN-LAST:event_ClearButtonActionPerformed

    private void ViewFamilyRatingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ViewFamilyRatingsButtonActionPerformed
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
    }//GEN-LAST:event_ViewFamilyRatingsButtonActionPerformed

    private void GenreFilterComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GenreFilterComboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_GenreFilterComboActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
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
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new AdultFrame(1).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddMovieButton;
    private javax.swing.JButton AnalyticsButton;
    private javax.swing.JPanel Buttons;
    private javax.swing.JButton ClearButton;
    private javax.swing.JButton DeleteMovieButton;
    private javax.swing.JButton EdditMovieButton;
    private javax.swing.JComboBox<String> GenreFilterCombo;
    private javax.swing.JButton ManageUsersButton;
    private javax.swing.JButton ModerateContentButton;
    private javax.swing.JPanel Movie;
    private javax.swing.JScrollPane MovieScrollPane;
    private javax.swing.JButton RefreshButton;
    private javax.swing.JPanel Search;
    private javax.swing.JButton SearchButton;
    private javax.swing.JTextField SearchTextField;
    private javax.swing.JButton SetRestrictionButton;
    private javax.swing.JButton ViewFamilyRatingsButton;
    // End of variables declaration//GEN-END:variables
}
