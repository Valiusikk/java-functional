package com.endava.internship.service;

import com.endava.internship.domain.Privilege;
import com.endava.internship.domain.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserServiceImpl implements UserService {

    @Override
    public List<String> getFirstNamesReverseSorted(List<User> users) {
        return users.stream()
                .sorted((userA, userB) -> userB.getFirstName().length() - userA.getFirstName().length())
                .map(user -> user.getFirstName())
                .collect(Collectors.toList());

    }

    @Override
    public List<User> sortByAgeDescAndNameAsc(final List<User> users) {
        return users.stream()
                .sorted((userA, userB) -> userB.getAge() - userA.getAge()
                        - userA.getFirstName().length() - userB.getFirstName().length())
                .collect(Collectors.toList());
    }

    @Override
    public List<Privilege> getAllDistinctPrivileges(final List<User> users) {
        final List<Privilege> list = new ArrayList<>();
        users.forEach(user -> users.get(users.indexOf(user)).getPrivileges().stream().collect(Collectors.toCollection(() -> list)));
        return list;
    }

    @Override
    public Optional<User> getUpdateUserWithAgeHigherThan(final List<User> users, final int age) {
        return users.stream().reduce((userA, userB) -> {
            if (userB.getAge() > age && userB.getPrivileges().contains(Privilege.UPDATE)) {
                return userB;
            } else {
                return userA;
            }
        });
    }

    @Override
    public Map<Integer, List<User>> groupByCountOfPrivileges(final List<User> users) {
        final Map<Integer, List<User>> map = new HashMap<>();
        users.stream().forEach(user -> {
            if (map.containsKey(user.getPrivileges().size())) {
                final List<User> list = map.get(user.getPrivileges().size());
                list.add(user);
            } else {
                final List<User> list = new ArrayList<>();
                list.add(user);
                map.put(user.getPrivileges().size(), list);
            }
        });
        return map;
    }

    @Override
    public double getAverageAgeForUsers(final List<User> users) {
        if (Objects.isNull(users) || users.equals(Collections.emptyList())) {
            return -1;
        }
        return users.stream().
                map(user -> user.getAge()).mapToInt(Integer::intValue).
                average().getAsDouble();
    }

    @Override
    public Optional<String> getMostFrequentLastName(final List<User> users) {
        if (users.equals(Collections.emptyList()) || Objects.isNull(users)) {
            return Optional.empty();
        }
        final AtomicInteger counter = new AtomicInteger();
        final Map<String, Integer> map = new HashMap<>();
        users.forEach(user -> {
            Integer integer = map.get(user.getLastName());
            if (Objects.nonNull(integer)) {
                counter.getAndIncrement();
                map.replace(user.getLastName(), integer, ++integer);
            } else {
                map.put(user.getLastName(), 1);
            }
        });
        map.values().forEach(System.out::println);
        System.out.println(counter.get());
        if (map.values().stream().allMatch(a -> a == counter.get() + 1)) {
            return Optional.empty();
        }
        final Optional<Map.Entry<String, Integer>> resultOptional
                = map.entrySet().
                stream().
                max(Comparator.comparingInt(Map.Entry::getValue));
        return resultOptional.map(Map.Entry::getKey);
    }

    @Override
    public List<User> filterBy(final List<User> users, final Predicate<User>... predicates) {
        Stream<User> userStream = users.stream();
        for (Predicate<User> predicate : predicates) {
            userStream = userStream.filter(predicate);
        }
        return userStream.collect(Collectors.toList());
    }

    @Override
    public String convertTo(final List<User> users, final String delimiter, final Function<User, String> mapFun) {
        final StringBuilder stringBuilder = new StringBuilder();
        users.stream().forEach(user -> {
            stringBuilder.append(mapFun.apply(user));
            if (users.indexOf(user) < users.size() - 1) {
                stringBuilder.append(delimiter);
            }
        });
        return stringBuilder.toString();
    }

    @Override
    public Map<Privilege, List<User>> groupByPrivileges(List<User> users) {
        final Map<Privilege, List<User>> map = new HashMap<>();
        users.stream().forEach(user -> {
            user.getPrivileges().forEach(privilege -> {
                if (map.containsKey(privilege)) {
                    final List<User> list = map.get(privilege);
                    list.add(user);
                    map.put(privilege, list);
                } else {
                    final List<User> list = new ArrayList<>();
                    list.add(user);
                    map.put(privilege, list);
                }
            });
        });
        return map;
    }

    @Override
    public Map<String, Long> getNumberOfLastNames(final List<User> users) {
        final Map<String, Long> map = new HashMap<>();
        users.stream().forEach(user -> {
            if (map.containsKey(user.getLastName())) {
                long number = map.get(user.getLastName());
                map.put(user.getLastName(), ++number);
            } else {
                map.put(user.getLastName(), 1L);
            }
        });
        return map;
    }
}
