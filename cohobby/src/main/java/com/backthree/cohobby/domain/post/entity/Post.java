package com.backthree.cohobby.domain.post.entity;

import com.backthree.cohobby.domain.common.BaseTimeEntity;
import com.backthree.cohobby.domain.hobby.entity.Hobby;
import com.backthree.cohobby.domain.rent.entity.Rent;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;
import com.backthree.cohobby.global.exception.GeneralException;
import jakarta.persistence.*;
import lombok.*;
import com.backthree.cohobby.domain.post.dto.request.UpdateDetailRequest;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 63)
    private String goods;

    @Column(length = 500)
    private String defectStatus;

    @Column(length = 500)
    private String caution;

    @Column private Integer dailyPrice;
    @Column private Integer weeklyPrice;
    @Column private Integer deposit;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PostStatus status = PostStatus.DRAFT;

    @Column(name = "available_from", columnDefinition = "DATE")
    private LocalDate availableFrom;

    @Column(name = "available_until", columnDefinition = "DATE")
    private LocalDate availableUntil;

    @Column(name = "purchased_at", columnDefinition="DATE")
    private LocalDate purchasedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hobbyId")
    private Hobby hobby;

    @OneToMany(mappedBy = "post")
    private Set<Image> images = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(name = "likes",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users = new LinkedHashSet<>();

    @OneToMany(mappedBy = "post")
    private Set<Rent> rents = new LinkedHashSet<>();


    public void validatePeriod() {
        if (purchasedAt != null && purchasedAt.isAfter(LocalDate.now())) {
            throw new GeneralException(ErrorStatus.PURCHASE_DATE_IN_FUTURE);
        }
        if (availableFrom != null && availableUntil != null &&
                availableFrom.isAfter(availableUntil)) {
            throw new GeneralException(ErrorStatus.AVAILABLE_PERIOD_INVALID);
        }
    }

}