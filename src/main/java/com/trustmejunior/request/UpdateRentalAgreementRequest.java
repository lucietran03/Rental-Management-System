package com.trustmejunior.request;

/**
 * @author TrustMeJunior
 */

import java.util.Date;

import com.trustmejunior.model.Enum.RentalPeriod;
import com.trustmejunior.model.Enum.RentalStatus;

public class UpdateRentalAgreementRequest {
    private double fee;
    private Date startDate;
    private Date endDate;
    private RentalPeriod period;
    private RentalStatus status;
    private int ownerId;
    private int propertyId;

    public UpdateRentalAgreementRequest(double fee, Date startDate, Date endDate,
            RentalPeriod period, RentalStatus status, int ownerId, int propertyId) {
        this.fee = fee;
        this.startDate = startDate;
        this.endDate = endDate;
        this.period = period;
        this.status = status;
        this.ownerId = ownerId;
        this.propertyId = propertyId;
    }

    public double getFee() {
        return fee;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public RentalPeriod getPeriod() {
        return period;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setPeriod(RentalPeriod period) {
        this.period = period;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }
}
