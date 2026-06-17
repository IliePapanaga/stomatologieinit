package com.cl.mdd.server.core.settings;

public final class  Settings {

    public static final class PaymentPrimeRateSettings {

        public static final Setting<String> LOGIN = new StringSetting("payments.prime_rate_specifics.login");
        public static final Setting<String> PASSWORD = new StringSetting("payments.prime_rate_specifics.password");
        public static final Setting<String> API_KEY = new StringSetting("payments.prime_rate_specifics.api_key");

        public static final Setting[] SETTINGS = new Setting[] {LOGIN, PASSWORD, API_KEY};
    }

    public static final class PaymentAttemptsSettings {

        public static final Setting<Long> NUMBER_OF_ATTEMPTS_ACH = new LongSetting("payments.attempts.number_of_attempts_ach");
        public static final Setting<Long> NUMBER_OF_ATTEMPTS_CARD = new LongSetting("payments.attempts.number_of_attempts_card");
        public static final Setting<Long> INTERVAL = new LongSetting("payments.attempts.interval_days");
        public static final Setting<Long> INTERVAL_ACH_CHECK = new LongSetting("payments.attempts.ach_check_days");
        public static final Setting<Long> ACH_PENALTY = new LongSetting("payments.attempts.ach_penalty_fee");
        public static final Setting<Long> CC_PENALTY = new LongSetting("payments.attempts.cc_penalty_fee");
        public static final Setting<Long> MATURITY_MINUTES = new LongSetting("payment.attempts.maturity_new_minutes");

        public static final Setting[] SETTINGS = new Setting[] {
                NUMBER_OF_ATTEMPTS_ACH, NUMBER_OF_ATTEMPTS_CARD, INTERVAL, ACH_PENALTY, CC_PENALTY, MATURITY_MINUTES };
    }

    public static final class FeeTemporaryJobSettings {
        public static final Setting<Long> COMPENSATION_RDA = new LongSetting("job_fees.temporary.compensation_rda");
        public static final Setting<Long> COMPENSATION_RDH = new LongSetting("job_fees.temporary.compensation_rdh");
        public static final Setting<Long> COMPENSATION_DDS = new LongSetting("job_fees.temporary.compensation_dds");
        public static final Setting<Long> COMPENSATION_SPECIALIST = new LongSetting("job_fees.temporary.compensation_specialist");

        public static final Setting[] SETTINGS = new Setting[] { COMPENSATION_RDA, COMPENSATION_RDH, COMPENSATION_DDS, COMPENSATION_SPECIALIST};
    }

    public static final class FeePermanentJobSettings {
        public static final Setting<Long> WEEKS_PER_YEAR = new LongSetting("job_fees.permanent.weeks_per_year");
        public static final Setting<Long> PERCENT_FOR_HIRING_OTHERS = new LongSetting("job_fees.permanent.percentage_for_hiring_others");
        public static final Setting<Long> PERCENT_FOR_HIRING_DENTISTS = new LongSetting("job_fees.permanent.percentage_for_hiring_dentists");

        public static final Setting[] SETTINGS = new Setting[] { WEEKS_PER_YEAR, PERCENT_FOR_HIRING_OTHERS, PERCENT_FOR_HIRING_DENTISTS};
    }

    public static final class PostingSettings {

        public static final Setting<Long> ALLOWED_NO_SHOW = new LongSetting("postings.default.allowed_no_show");
        public static final Setting<Long> ALLOWED_REJECTIONS = new LongSetting("postings.default.allowed_rejections");
        public static final Setting<Long> PAYMENT_START_AFTER_STARTING_JOB = new LongSetting("postings.default.payment_start_after_starting_job");

        public static final Setting[] SETTINGS = new Setting[] { ALLOWED_NO_SHOW, ALLOWED_REJECTIONS, PAYMENT_START_AFTER_STARTING_JOB };
    }

    public static class BooleanSetting extends BaseSetting<Boolean> {
        public BooleanSetting(String key) {
            super(key, Boolean.class);
        }
    }

    public static class StringSetting extends BaseSetting<String> {
        public StringSetting(String key) {
            super(key, String.class);
        }
    }

    public static class LongSetting extends BaseSetting<Long> {
        public LongSetting(String key) {
            super(key, Long.class);
        }
    }

    private abstract static class BaseSetting<T> implements Setting<T> {

        private String key;

        private Class<T> type;

        public BaseSetting(String key, Class<T> type) {
            this.key = key;
            this.type = type;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Class<T> getType() {
            return type;
        }
    }

    public interface Setting<T> {

        String getKey();

        Class<T> getType();
    }
}
