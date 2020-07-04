package com.dvproject.vertTerm.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Joshua Müller
 */

public class AuthorityTester {

	public static void containsAll(String... neededAuthorities) {
		List<GrantedAuthority> grantedAuthorities = createSimpleAuthorities(Arrays.asList(neededAuthorities));

		containsAll(grantedAuthorities);
	}

	public static void containsAll(GrantedAuthority... neededAuthorities) {
		containsAll(Arrays.asList(neededAuthorities));
	}

	public static void containsAll(List<GrantedAuthority> neededAuthorities) {
		Collection<? extends GrantedAuthority> authorities = getAuthoritiesOfSession();

		if (!neededAuthorities.stream().allMatch(auth -> authorities.contains(auth)))
			throw new RuntimeException("Not enough rights");
	}

	public static void containsAny(String... neededAuthorities) {
		List<GrantedAuthority> grantedAuthorities = createSimpleAuthorities(Arrays.asList(neededAuthorities));

		containsAny(grantedAuthorities);
	}

	public static void containsAny(GrantedAuthority... neededAuthorities) {
		containsAny(Arrays.asList(neededAuthorities));
	}

	public static void containsAny(List<GrantedAuthority> neededAuthorities) {
		Collection<? extends GrantedAuthority> authorities = getAuthoritiesOfSession();

		if (!neededAuthorities.stream().anyMatch(auth -> authorities.contains(auth)))
			throw new RuntimeException("Not enough rights");
	}

	private static List<GrantedAuthority> createSimpleAuthorities(List<String> authorityNames) {
		return authorityNames.stream().map(auth -> new SimpleGrantedAuthority(auth)).collect(Collectors.toList());
	}

	private static Collection<? extends GrantedAuthority> getAuthoritiesOfSession() {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
	}

}