package moviecriticssystem;

import java.awt.*;
import java.io.File;
import javax.swing.*;

public class Movie extends JPanel {

    private final int movieId;

    public Movie(int id, String title, String poster, int rating,
                 String leadActor, boolean restricted, String genre) {
        this.movieId = id;

        Dimension cardSize = new Dimension(170, 310);
        setPreferredSize(cardSize);
        setMinimumSize(cardSize);
        setMaximumSize(cardSize);
        setLayout(new BorderLayout(0, 2));
        Theme.styleCard(this);

        // Poster image
        Dimension imgSize = new Dimension(170, 200);
        JLabel img = new JLabel("No Poster", SwingConstants.CENTER);
        img.setPreferredSize(imgSize);
        img.setMinimumSize(imgSize);
        img.setMaximumSize(imgSize);
        img.setOpaque(true);
        img.setBackground(Theme.SURFACE);
        img.setForeground(Theme.TEXT_DIM);

        File f = new File("posters/" + (poster != null ? poster : ""));
        if (poster != null && !poster.isEmpty() && f.exists()) {
            Image scaled = new ImageIcon(f.getAbsolutePath())
                    .getImage().getScaledInstance(170, 200, Image.SCALE_SMOOTH);
            img.setIcon(new ImageIcon(scaled));
            img.setText("");
        }
        add(img, BorderLayout.CENTER);

        // Info panel
        JPanel info = new JPanel(new GridLayout(5, 1, 0, 1));
        info.setBackground(Theme.BG);
        info.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setForeground(Theme.TEXT);
        titleLbl.setFont(titleLbl.getFont().deriveFont(Font.BOLD, 12f));

        JLabel actorLbl = new JLabel("Star: " + (leadActor != null ? leadActor : "-"), SwingConstants.CENTER);
        actorLbl.setForeground(Theme.TEXT_DIM);

        JLabel ratingLbl = new JLabel("Rating: " + rating + "/10", SwingConstants.CENTER);
        ratingLbl.setForeground(Theme.TEXT);

        // Age restriction — enum tag: red only if restricted
        JLabel ageLbl = new JLabel(restricted ? "RESTRICTED" : "FAMILY", SwingConstants.CENTER);
        ageLbl.setOpaque(true);
        ageLbl.setBackground(restricted ? Theme.RED : Theme.SURFACE2);
        ageLbl.setForeground(Theme.TEXT);
        ageLbl.setFont(ageLbl.getFont().deriveFont(Font.BOLD, 10f));

        // Genre — enum tag: dark surface, no red
        JLabel genreLbl = new JLabel(genre != null ? genre.toUpperCase() : "N/A", SwingConstants.CENTER);
        genreLbl.setOpaque(true);
        genreLbl.setBackground(Theme.SURFACE2);
        genreLbl.setForeground(Theme.TEXT_DIM);
        genreLbl.setFont(genreLbl.getFont().deriveFont(Font.BOLD, 10f));

        info.add(titleLbl);
        info.add(actorLbl);
        info.add(ratingLbl);
        info.add(ageLbl);
        info.add(genreLbl);
        add(info, BorderLayout.SOUTH);
    }

    public int getMovieId() {
        return movieId;
    }
}
