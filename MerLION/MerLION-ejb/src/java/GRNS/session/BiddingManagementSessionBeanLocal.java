/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GRNS.session;

import GRNS.entity.Bid;
import java.util.List;
import javax.ejb.Local;
import util.exception.BidInvalidException;

/**
 *
 * @author chongyangsun
 */
@Local
public interface BiddingManagementSessionBeanLocal {
    public Bid createNewBid(Long accountId, Long postId, String postType, Double bid) throws BidInvalidException;
    public Bid retrieveWinningBid(Long postId, String postType);
    public Bid retrieveNextWinningBid(Long postId, String postType);
    public Integer retrieveBidsCount(Long postId, String postType);
    public List<Bid> retrieveAllBids(Long postId, String postType);
    public Boolean retractingBid(Long accountId, Long postId, String postType);//not allowed yet
    public Boolean updateBid(Long accountId, Long postId, String postType, Double bid);//do not use
    public Boolean completeBid(Long postId, String postType);
    public Boolean checkBidAlreadyExist(Long accountId, Long postId, String postType);
    public Bid createBlindBid(Long accountId, Long postId, String postType, Double bid) throws BidInvalidException;
}
