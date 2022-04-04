package com.endava.internship.service;

import com.endava.internship.domain.Privilege;
import com.endava.internship.domain.User;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    @Override
    public List<String> getFirstNamesReverseSorted(List<User> users) {
        return users.stream().
                map(User::getFirstName).
                sorted(Comparator.reverseOrder()).
                collect(Collectors.toList());
    }

    @Override
    public List<User> sortByAgeDescAndNameAsc(final List<User> users) {
        return users.stream().
                sorted(Comparator.comparing(User::getFirstName)).
                sorted(Comparator.comparing(User::getAge).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Privilege> getAllDistinctPrivileges(final List<User> users) {
        return users.stream().
                map(User::getPrivileges).
                flatMap(List::stream).
                collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUpdateUserWithAgeHigherThan(final List<User> users, final int age) {
        return users.stream().
                filter(user -> user.getAge() > age && user.getPrivileges().contains(Privilege.UPDATE))
                .findFirst();
    }

    @Override
    public Map<Integer, List<User>> groupByCountOfPrivileges(final List<User> users) {
        return users.stream().collect(Collectors.groupingBy(user -> user.getPrivileges().size()));
    }

    @Override
    public double getAverageAgeForUsers(final List<User> users) {
        return users.stream().
                map(User::getAge).
                mapToInt(Integer::intValue).
                average().orElse(-1);
    }

    @Override
    public Optional<String> getMostFrequentLastName(final List<User> users) {
        return users.stream().
                collect(Collectors.groupingBy(User::getLastName, Collectors.counting())).//group by lastname and count how many times it repeats
                        entrySet().stream().filter(node -> node.getValue() >= 2).//put condition that count should be more than 1
                        reduce((a, b) -> a.getValue() < b.getValue() ? b ://look for node with biggest count
                        a.getValue() > b.getValue() ? a : new AbstractMap.SimpleImmutableEntry<>(null, a.getValue())).//else create new entry with null key,
                // and result optional will be empty
                        map(Map.Entry::getKey);//get the Optional that contains count of most frequent lastname
    }

    @Override
    public List<User> filterBy(final List<User> users, final Predicate<User>... predicates) {
        return users.stream().
                filter(Arrays.stream(predicates).reduce(predicate -> true, Predicate::and)).
                collect(Collectors.toList());
    }

    @Override
    public String convertTo(final List<User> users, final String delimiter, final Function<User, String> mapFun) {
        return users.stream().map(mapFun).collect(Collectors.joining(delimiter));
    }

    @Override
    public Map<Privilege, List<User>> groupByPrivileges(List<User> users) {
        return users.stream().flatMap(user -> user.getPrivileges().stream().
                map(privilege -> new AbstractMap.SimpleEntry<Privilege, User>(privilege, user) {})).//create node for each privilege from lists of privileges of all users
                collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));//group by privileges and form list of
        //the appropriate users
    }

    @Override
    public Map<String, Long> getNumberOfLastNames(final List<User> users) {
//        return users.stream().collect(
//                HashMap::new, (map, value) -> map.put(value.getLastName(), map.getOrDefault(value.getLastName(), 0L) + 1L), HashMap::putAll);
        //the same
        return users.stream().collect(Collectors.toMap(User::getLastName, user -> 1L, Long::sum));
    }
}
