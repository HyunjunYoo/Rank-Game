package rank.game.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Setter
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_name", nullable = false)
    private String gameName;

    @Column(name = "game_vote", nullable = false)
    @ColumnDefault("0")
    private int gameVote = 0;

    @Column(name = "game_rank", nullable = false)
    @ColumnDefault("0")
    private Integer gameRank = 0;


    @Column(name = "game_image", nullable = false)
    private String imageUrl;

}
