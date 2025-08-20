package com.alejandro.microservices.notifications.service;

import com.alejandro.microservices.notifications.dto.ReportDTOs.*;
import com.alejandro.microservices.notifications.repository.NotificationLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final NotificationLogRepository logRepository;

    public ReportService(NotificationLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public GeneralStats getGeneralStats() {
        long totalNotifications = logRepository.count();
        long successfulNotifications = logRepository.countSuccessfulByDate(LocalDate.now());
        long failedNotifications = logRepository.countFailures();
        Double successRate = logRepository.getSuccessRate();
        Double averageProcessingTime = logRepository.getAverageProcessingTime();

        return new GeneralStats(
            totalNotifications,
            successfulNotifications,
            failedNotifications,
            successRate != null ? successRate : 0.0,
            averageProcessingTime != null ? averageProcessingTime : 0.0
        );
    }

    public List<ChannelStats> getChannelStats() {
        List<Object[]> channelCounts = logRepository.countByChannel();
        List<Object[]> successfulCounts = logRepository.countSuccessfulByChannel();
        List<Object[]> processingTimes = logRepository.getAverageProcessingTimeByChannel();
        List<Object[]> successRates = logRepository.getSuccessRateByChannel();

        Map<String, Long> channelCountMap = channelCounts.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1]
            ));

        Map<String, Long> successfulCountMap = successfulCounts.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1]
            ));

        Map<String, Double> processingTimeMap = processingTimes.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Double) row[1]
            ));

        Map<String, Double> successRateMap = successRates.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Double) row[1]
            ));

        return channelCountMap.entrySet().stream()
            .map(entry -> new ChannelStats(
                entry.getKey(),
                entry.getValue(),
                successfulCountMap.getOrDefault(entry.getKey(), 0L),
                successRateMap.getOrDefault(entry.getKey(), 0.0),
                processingTimeMap.getOrDefault(entry.getKey(), 0.0)
            ))
            .sorted(Comparator.comparing(ChannelStats::getCount).reversed())
            .collect(Collectors.toList());
    }

    public List<UserStats> getTopUsers(int limit) {
        List<Object[]> userCounts = logRepository.countByUser();

        return userCounts.stream()
            .limit(limit)
            .map(row -> new UserStats(
                (String) row[0],
                (Long) row[1],
                LocalDate.now().toString() // Simplificado - podrías hacer una consulta más específica
            ))
            .collect(Collectors.toList());
    }

    public List<TypeStats> getTypeStats() {
        List<Object[]> typeCounts = logRepository.countByType();
        long totalNotifications = logRepository.count();

        return typeCounts.stream()
            .map(row -> new TypeStats(
                (String) row[0],
                (Long) row[1],
                totalNotifications > 0 ? ((Long) row[1] * 100.0 / totalNotifications) : 0.0
            ))
            .sorted(Comparator.comparing(TypeStats::getCount).reversed())
            .collect(Collectors.toList());
    }

    public List<PriorityStats> getPriorityStats() {
        List<Object[]> priorityCounts = logRepository.countByPriority();
        long totalNotifications = logRepository.count();

        return priorityCounts.stream()
            .map(row -> new PriorityStats(
                (String) row[0],
                (Long) row[1],
                totalNotifications > 0 ? ((Long) row[1] * 100.0 / totalNotifications) : 0.0
            ))
            .sorted(Comparator.comparing(PriorityStats::getCount).reversed())
            .collect(Collectors.toList());
    }

    public List<TrendPoint> getWeeklyTrend() {
        LocalDateTime fromDate = LocalDateTime.now().minusDays(7);
        List<Object[]> trendData = logRepository.getDailyTrend(fromDate);

        return trendData.stream()
            .map(row -> new TrendPoint(
                row[0].toString(),
                (Long) row[1]
            ))
            .collect(Collectors.toList());
    }

    public DailyReport getDailyReport(LocalDate date) {
        long totalNotifications = logRepository.countByDate(date);
        long successfulNotifications = logRepository.countSuccessfulByDate(date);

        List<Object[]> hourlyData = logRepository.getHourlyTrend(date);
        List<TrendPoint> hourlyDistribution = hourlyData.stream()
            .map(row -> new TrendPoint(
                "Hora " + row[0].toString(),
                (Long) row[1]
            ))
            .collect(Collectors.toList());

        // Para simplificar, usamos datos generales por canal y tipo
        Map<String, Long> channelDistribution = logRepository.countByChannel().stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1]
            ));

        Map<String, Long> typeDistribution = logRepository.countByType().stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1]
            ));

        return new DailyReport(
            date,
            totalNotifications,
            successfulNotifications,
            hourlyDistribution,
            channelDistribution,
            typeDistribution
        );
    }

    public ComprehensiveReport getComprehensiveReport() {
        return new ComprehensiveReport(
            getGeneralStats(),
            getChannelStats(),
            getTopUsers(10),
            getTypeStats(),
            getPriorityStats(),
            getWeeklyTrend()
        );
    }

    public long getDailyCount(LocalDate date) {
        return logRepository.countByDate(date);
    }

    public long getUserDailyCount(String userId, LocalDate date) {
        return logRepository.countByUserAndDate(userId, date);
    }

    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        metrics.put("averageProcessingTime", logRepository.getAverageProcessingTime());
        metrics.put("successRate", logRepository.getSuccessRate());
        metrics.put("totalFailures", logRepository.countFailures());

        List<Object[]> channelPerformance = logRepository.getAverageProcessingTimeByChannel();
        Map<String, Double> channelProcessingTimes = channelPerformance.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Double) row[1]
            ));
        metrics.put("processingTimeByChannel", channelProcessingTimes);

        return metrics;
    }
}
