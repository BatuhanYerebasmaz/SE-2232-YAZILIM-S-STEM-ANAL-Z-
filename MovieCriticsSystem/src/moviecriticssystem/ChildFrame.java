/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package moviecriticssystem;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import javax.swing.JOptionPane;
import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;
/**
 *
 * @author yereb
 */
public class ChildFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ChildFrame.class.getName());
    private int userId;
    /**
     * Creates new form ChildFrame
     */

public ChildFrame(int userId) {
   this.userId = userId; 
    initComponents();
    
    posterLabel.setText("No Poster");
    posterLabel.setPreferredSize(new java.awt.Dimension(150, 200));
    posterLabel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.GRAY));
    posterLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    
    loadMovies();
    loadGenres();
    
    jTable1.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            int row = jTable1.getSelectedRow();
            if (row == -1) return;
            int movieId = (int) jTable1.getValueAt(row, 0);
            try (Connection conn = DatabaseConnection.connect()) {
                PreparedStatement ps = conn.prepareStatement(
                    "SELECT Poster FROM Movies WHERE MovieID = ?");
                ps.setInt(1, movieId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) showPoster(rs.getString("Poster"));
            } catch (Exception ex) {
                posterLabel.setText("No Poster");
            }
        }
    });
}
private void showPoster(String posterPath) {
    if (posterPath == null || posterPath.isEmpty()) {
        posterLabel.setIcon(null);
        posterLabel.setText("No Poster");
        return;
    }
    
    File file = new File("posters/" + posterPath);
    if (!file.exists()) {
        posterLabel.setIcon(null);
        posterLabel.setText("No Poster");
        return;
    }
    
    ImageIcon icon = new ImageIcon(file.getAbsolutePath());
    Image scaled = icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
    posterLabel.setIcon(new ImageIcon(scaled));
    posterLabel.setText("");
}
private void loadMovies() {
      DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    model.setRowCount(0);

    String query = 
        "SELECT m.MovieID, m.Title, m.Genre, m.Language, m.CountryOfOrigin, m.ReleaseDate, " +
        "m.Rating, m.Watched, " +
        "CONCAT(d.FirstName, ' ', d.LastName) AS Director, " +
        "CONCAT(la.FirstName, ' ', la.LastName) AS LeadingActor, " +
        "CONCAT(sa.FirstName, ' ', sa.LastName) AS SupportingActor, " +
        "m.Comments " +
        "FROM Movies m " +
        "LEFT JOIN Persons d  ON m.DirectorId = d.PersonID " +
        "LEFT JOIN Persons la ON m.LeadingActorId = la.PersonID " +
        "LEFT JOIN Persons sa ON m.SupportingActorId = sa.PersonID " +
        "WHERE m.ParentalRestriction = FALSE";

    try (Connection conn = DatabaseConnection.connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("MovieID"),
                rs.getString("Title"),
                rs.getString("Genre"),
                rs.getString("Language"),
                rs.getString("CountryOfOrigin"),
                rs.getDate("ReleaseDate"),
                rs.getInt("Rating"),
                rs.getBoolean("Watched"),
                rs.getString("Director"),
                rs.getString("LeadingActor"),
                rs.getString("SupportingActor"),
                rs.getString("Comments")
            });
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
                "UPDATE UserMovieInteractions SET " + field + "=? WHERE UserID=? AND MovieID=?");
            if (value instanceof Integer) upd.setInt(1, (Integer) value);
            else if (value instanceof Boolean) upd.setBoolean(1, (Boolean) value);
            else upd.setString(1, (String) value);
            upd.setInt(2, userId);
            upd.setInt(3, movieId);
            upd.executeUpdate();
        } else {
            PreparedStatement ins = conn.prepareStatement(
                "INSERT INTO UserMovieInteractions (UserID, MovieID, " + field + ") VALUES (?,?,?)");
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

private void loadGenres() {
    GenreFilterCombo.addItem("All");
    try (Connection conn = DatabaseConnection.connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT DISTINCT Genre FROM Movies WHERE ParentalRestriction = FALSE")) {

        while (rs.next()) {
            GenreFilterCombo.addItem(rs.getString("Genre"));
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        MarkWatchedButton = new javax.swing.JButton();
        RateMovieButton = new javax.swing.JButton();
        AddCommentButton = new javax.swing.JButton();
        WatchlistButton = new javax.swing.JButton();
        ProgressButton = new javax.swing.JButton();
        FamilyRatingsButton = new javax.swing.JButton();
        RefreshButton = new javax.swing.JButton();
        GenreFilterCombo = new javax.swing.JComboBox<>();
        SearchTextField = new javax.swing.JTextField();
        SearchButton1 = new javax.swing.JButton();
        ClearButton1 = new javax.swing.JButton();
        posterLabel = new javax.swing.JLabel();
        AddWatchlistButton = new javax.swing.JButton();
        RemoveWatchlistButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Title", "Genre", "Language", "Country", "Release Date", "Rating", "Watched", "Director", "Lead Actor", "Support Actor", "Comments"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        MarkWatchedButton.setText("Mark Watched");
        MarkWatchedButton.addActionListener(this::MarkWatchedButtonActionPerformed);

        RateMovieButton.setText("Rate Movie");
        RateMovieButton.addActionListener(this::RateMovieButtonActionPerformed);

        AddCommentButton.setText("Add Comment");
        AddCommentButton.addActionListener(this::AddCommentButtonActionPerformed);

        WatchlistButton.setText("My Watchlist");
        WatchlistButton.addActionListener(this::WatchlistButtonActionPerformed);

        ProgressButton.setText("My Progress");
        ProgressButton.addActionListener(this::ProgressButtonActionPerformed);

        FamilyRatingsButton.setText("Family Ratings");
        FamilyRatingsButton.addActionListener(this::FamilyRatingsButtonActionPerformed);

        RefreshButton.setText("Refresh");
        RefreshButton.addActionListener(this::RefreshButtonActionPerformed);

        SearchButton1.setText("Search");
        SearchButton1.addActionListener(this::SearchButton1ActionPerformed);

        ClearButton1.setText("Clear");
        ClearButton1.addActionListener(this::ClearButton1ActionPerformed);

        posterLabel.setText("jLabel1");

        AddWatchlistButton.setText("Add  Watchlist");
        AddWatchlistButton.addActionListener(this::AddWatchlistButtonActionPerformed);

        RemoveWatchlistButton.setText("Remove Watchlist");
        RemoveWatchlistButton.addActionListener(this::RemoveWatchlistButtonActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(GenreFilterCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SearchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SearchButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ClearButton1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(482, 482, 482)
                .addComponent(posterLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(106, Short.MAX_VALUE)
                .addComponent(MarkWatchedButton, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FamilyRatingsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RateMovieButton, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AddCommentButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(WatchlistButton, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AddWatchlistButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(RemoveWatchlistButton)
                .addGap(18, 18, 18)
                .addComponent(ProgressButton, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(RefreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(106, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SearchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SearchButton1)
                    .addComponent(GenreFilterCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ClearButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ProgressButton)
                        .addComponent(MarkWatchedButton)
                        .addComponent(RefreshButton)
                        .addComponent(AddWatchlistButton)
                        .addComponent(RemoveWatchlistButton))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(RateMovieButton)
                        .addComponent(FamilyRatingsButton)
                        .addComponent(AddCommentButton)
                        .addComponent(WatchlistButton)))
                .addGap(47, 47, 47)
                .addComponent(posterLabel)
                .addGap(134, 134, 134))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SearchButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchButton1ActionPerformed
        String keyword = SearchTextField.getText().trim();
    String genre = (String) GenreFilterCombo.getSelectedItem();

    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    model.setRowCount(0);

    String sql = "SELECT MovieID, Title, Genre, Language, ReleaseDate, Rating, Watched, Comments FROM Movies WHERE ParentalRestriction = FALSE";

    if (!keyword.isEmpty()) {
        sql += " AND (Title LIKE '%" + keyword + "%' OR Language LIKE '%" + keyword + "%')";
    }
    if (genre != null && !genre.equals("All")) {
        sql += " AND Genre = '" + genre + "'";
    }

    try (Connection conn = DatabaseConnection.connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("MovieID"),
                rs.getString("Title"),
                rs.getString("Genre"),
                rs.getString("Language"),
                rs.getDate("ReleaseDate"),
                rs.getInt("Rating"),
                rs.getBoolean("Watched"),
                rs.getString("Comments")
            });
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
    }//GEN-LAST:event_SearchButton1ActionPerformed

    private void ClearButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearButton1ActionPerformed
    SearchTextField.setText("");
    GenreFilterCombo.setSelectedIndex(0);
    loadMovies();
    }//GEN-LAST:event_ClearButton1ActionPerformed

    private void MarkWatchedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MarkWatchedButtonActionPerformed
          int row = jTable1.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a movie first!");
        return;
    }

    int movieId = (int) jTable1.getValueAt(row, 0);

    upsertInteraction(movieId, "Watched", true);
    JOptionPane.showMessageDialog(this, "Marked as watched!");
    loadMovies();
    }//GEN-LAST:event_MarkWatchedButtonActionPerformed

    private void RateMovieButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RateMovieButtonActionPerformed
           int row = jTable1.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a movie first!");
        return;
    }

    int movieId = (int) jTable1.getValueAt(row, 0);

    String input = JOptionPane.showInputDialog(this, "Enter rating (1-10):");
    if (input == null || input.trim().isEmpty()) return;

    int score;
    try {
        score = Integer.parseInt(input.trim());
        if (score < 1 || score > 10) {
            JOptionPane.showMessageDialog(this, "Rating must be between 1-10!");
            return;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Enter a valid number!");
        return;
    }

    upsertInteraction(movieId, "Rating", score);
    JOptionPane.showMessageDialog(this, "Rating saved!");
    loadMovies();
    }//GEN-LAST:event_RateMovieButtonActionPerformed

    private void AddCommentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddCommentButtonActionPerformed
            int row = jTable1.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a movie first!");
        return;
    }

    int movieId = (int) jTable1.getValueAt(row, 0);

    String comment = JOptionPane.showInputDialog(this, "Enter your comment:");
    if (comment == null || comment.trim().isEmpty()) return;

        upsertInteraction(movieId, "Comment", comment.trim());
        JOptionPane.showMessageDialog(this, "Comment added!");
        loadMovies();   
    }//GEN-LAST:event_AddCommentButtonActionPerformed

    private void ProgressButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ProgressButtonActionPerformed
  try (Connection conn = DatabaseConnection.connect()) {
        // Total available movies
        PreparedStatement ps1 = conn.prepareStatement(
            "SELECT COUNT(*) as total FROM Movies WHERE ParentalRestriction = FALSE");
        ResultSet rs1 = ps1.executeQuery();
        int total = rs1.next() ? rs1.getInt("total") : 0;

        // Your watched count
        PreparedStatement ps2 = conn.prepareStatement(
            "SELECT COUNT(*) as watched FROM UserMovieInteractions " +
            "WHERE UserID = ? AND Watched = TRUE");
        ps2.setInt(1, userId);
        ResultSet rs2 = ps2.executeQuery();
        int myWatched = rs2.next() ? rs2.getInt("watched") : 0;

        // All family members (child users) watched counts
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
            sb.append(rs3.getString("Username"))
              .append(" : ")
              .append(rs3.getInt("watched"))
              .append(" watched\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString(),
                "My Progress", JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
    }//GEN-LAST:event_ProgressButtonActionPerformed
    
    
    
    private void FamilyRatingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FamilyRatingsButtonActionPerformed
       int row = jTable1.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a movie first!");
        return;
    }

    int movieId = (int) jTable1.getValueAt(row, 0);
    String movieTitle = (String) jTable1.getValueAt(row, 1);

    try (Connection conn = DatabaseConnection.connect()) {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT u.Username, i.Rating, i.Comment " +
            "FROM UserMovieInteractions i " +
            "JOIN Users u ON i.UserID = u.UserId " +
            "WHERE i.MovieID = ?");
        ps.setInt(1, movieId);
        ResultSet rs = ps.executeQuery();

        StringBuilder sb = new StringBuilder(movieTitle + "\n\n");
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

    private void RefreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshButtonActionPerformed
        loadMovies();
    }//GEN-LAST:event_RefreshButtonActionPerformed

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

        JOptionPane.showMessageDialog(this, sb.toString(),
                "My Watchlist", JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
    }//GEN-LAST:event_WatchlistButtonActionPerformed

    private void RemoveWatchlistButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveWatchlistButtonActionPerformed
        int row = jTable1.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a movie first!");
        return;
    }
    int movieId = (int) jTable1.getValueAt(row, 0);
    upsertInteraction(movieId, "Watchlist", false);
    JOptionPane.showMessageDialog(this, "Removed from watchlist!");
    }//GEN-LAST:event_RemoveWatchlistButtonActionPerformed

    private void AddWatchlistButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddWatchlistButtonActionPerformed
          int row = jTable1.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a movie first!");
        return;
    }
    int movieId = (int) jTable1.getValueAt(row, 0);
    upsertInteraction(movieId, "Watchlist", true);
    JOptionPane.showMessageDialog(this, "Added to watchlist!");
    }//GEN-LAST:event_AddWatchlistButtonActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new ChildFrame(1).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddCommentButton;
    private javax.swing.JButton AddWatchlistButton;
    private javax.swing.JButton ClearButton1;
    private javax.swing.JButton FamilyRatingsButton;
    private javax.swing.JComboBox<String> GenreFilterCombo;
    private javax.swing.JButton MarkWatchedButton;
    private javax.swing.JButton ProgressButton;
    private javax.swing.JButton RateMovieButton;
    private javax.swing.JButton RefreshButton;
    private javax.swing.JButton RemoveWatchlistButton;
    private javax.swing.JButton SearchButton1;
    private javax.swing.JTextField SearchTextField;
    private javax.swing.JButton WatchlistButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel posterLabel;
    // End of variables declaration//GEN-END:variables
}
