package app.dto;

import app.entity.Subject;

public class StudentAttendanceSummaryDto {
    private Subject subject;
    private long totalClasses;
    private long presentClasses;
    private long absentClasses;
    private double percentage;
    private boolean shortageWarning;

    public StudentAttendanceSummaryDto() {}

    public StudentAttendanceSummaryDto(Subject subject, long totalClasses, long presentClasses, long absentClasses, double percentage, boolean shortageWarning) {
        this.subject = subject;
        this.totalClasses = totalClasses;
        this.presentClasses = presentClasses;
        this.absentClasses = absentClasses;
        this.percentage = percentage;
        this.shortageWarning = shortageWarning;
    }

    public static StudentAttendanceSummaryDtoBuilder builder() {
        return new StudentAttendanceSummaryDtoBuilder();
    }

    public static class StudentAttendanceSummaryDtoBuilder {
        private Subject subject;
        private long totalClasses;
        private long presentClasses;
        private long absentClasses;
        private double percentage;
        private boolean shortageWarning;

        public StudentAttendanceSummaryDtoBuilder subject(Subject subject) { this.subject = subject; return this; }
        public StudentAttendanceSummaryDtoBuilder totalClasses(long total) { this.totalClasses = total; return this; }
        public StudentAttendanceSummaryDtoBuilder presentClasses(long present) { this.presentClasses = present; return this; }
        public StudentAttendanceSummaryDtoBuilder absentClasses(long absent) { this.absentClasses = absent; return this; }
        public StudentAttendanceSummaryDtoBuilder percentage(double pct) { this.percentage = pct; return this; }
        public StudentAttendanceSummaryDtoBuilder shortageWarning(boolean warning) { this.shortageWarning = warning; return this; }

        public StudentAttendanceSummaryDto build() {
            return new StudentAttendanceSummaryDto(subject, totalClasses, presentClasses, absentClasses, percentage, shortageWarning);
        }
    }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public long getTotalClasses() { return totalClasses; }
    public void setTotalClasses(long totalClasses) { this.totalClasses = totalClasses; }

    public long getPresentClasses() { return presentClasses; }
    public void setPresentClasses(long presentClasses) { this.presentClasses = presentClasses; }

    public long getAbsentClasses() { return absentClasses; }
    public void setAbsentClasses(long absentClasses) { this.absentClasses = absentClasses; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public boolean isShortageWarning() { return shortageWarning; }
    public void setShortageWarning(boolean shortageWarning) { this.shortageWarning = shortageWarning; }
}
