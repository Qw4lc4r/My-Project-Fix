package com.example.csharpmanualv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.*;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginUiTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> rule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void emptyFields_showsValidationMessage() {
        onView(withId(R.id.btnLogin)).perform(click());
        onView(withText("Вход в аккаунт")).check(matches(isDisplayed()));
    }

    @Test
    public void validInput_navigatesToMainActivity() {
        init();

        onView(withId(R.id.etEmail)).perform(replaceText("test@mail.com"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(replaceText("123456"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        intended(hasComponent(MainActivity.class.getName()));
        release();
    }
}
