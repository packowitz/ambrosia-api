alter table story rename column next_text to button_text;
alter table story alter column button_text set not null;
