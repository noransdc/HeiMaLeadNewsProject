package com.heima.model.articlecore.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleBehaviorAgg {


    private Long articleId;

    private int viewCount;
    private int likeCount;
    private int commentCount;
    private int collectCount;

    public void applyBehavior(int behaviorType) {
        switch (behaviorType) {
            case 1:
                viewCount++;
                break;
            case 2:
                likeCount++;
                break;
            case 3:
                likeCount = Math.max(0, likeCount - 1);
                break;
            case 4:
                commentCount++;
                break;
            case 5:
                collectCount++;
                break;
            case 6:
                collectCount = Math.max(0, collectCount - 1);
                break;
            default:
                break;
        }
    }


}
