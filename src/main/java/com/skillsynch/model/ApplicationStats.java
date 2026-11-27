package com.skillsynch.model;

/**
 * Numbers for the admin placement report.
 * Not a table: it replaces the Map&lt;String, Integer&gt; returned by
 * ApplicationDAO.getApplicationStatistics().
 */
public class ApplicationStats {

    private final int totalApplications;
    private final int selected;
    private final int shortlisted;
    private final int rejected;
    private final int offersExtended;

    public ApplicationStats(int totalApplications, int selected, int shortlisted,
                            int rejected, int offersExtended) {
        this.totalApplications = totalApplications;
        this.selected = selected;
        this.shortlisted = shortlisted;
        this.rejected = rejected;
        this.offersExtended = offersExtended;
    }

    public int getTotalApplications() { return totalApplications; }
    public int getSelected() { return selected; }
    public int getShortlisted() { return shortlisted; }
    public int getRejected() { return rejected; }
    public int getOffersExtended() { return offersExtended; }
}
